/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.server.bungee;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageHandler;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerPlayerCountHandler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineData;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.base.handshake.HandshakePacketTypes;
import net.lax1dude.eaglercraft.backend.server.bungee.PlatformPluginBungee.PluginMessageHandler;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BungeeListener implements Listener {

	private final PlatformPluginBungee plugin;

	BungeeListener(PlatformPluginBungee plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreLoginEvent(PreLoginEvent event) {
		PendingConnection conn = event.getConnection();
		Channel channel = BungeeUnsafe.getInitialHandlerChannel(conn);
		Attribute<IPipelineData> attr = channel.attr(PipelineAttributes.<IPipelineData>pipelineData());
		IPipelineData pipelineData = attr.get();
		if (pipelineData != null) {
			if (pipelineData.isEaglerPlayer() && conn.isOnlineMode()) {
				// avoid processing eagler players in a bad state...
				event.setReason(new TextComponent(HandshakePacketTypes.MSG_ONLINE_MODE));
				event.setCancelled(true);
				return;
			} else if (pipelineData.isCompressionDisable()) {
				BungeeUnsafe.injectCompressionDisable(conn);
			}
		}
		BungeeLoginData loginData = new BungeeLoginData(pipelineData, conn);
		attr.set(loginData);
		plugin.initializeLogin(loginData);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPostLoginEvent(PostLoginEvent event) {
		ProxiedPlayer player = event.getPlayer();
		IPipelineData data = BungeeUnsafe.getInitialHandlerChannel(player.getPendingConnection())
				.attr(PipelineAttributes.<IPipelineData>pipelineData()).getAndSet(null);
		IPipelineData pipelineData;
		if (data instanceof BungeeLoginData loginData) {
			pipelineData = loginData.getPipelineAttachment();
			if (loginData.eaglerPlayerProperty != (byte) 0 || loginData.texturesPropertyValue != null) {
				BungeeUnsafe.PropertyInjector injector = BungeeUnsafe.propertyInjector(player.getPendingConnection());
				if (loginData.texturesPropertyValue != null) {
					injector.injectTexturesProperty(loginData.texturesPropertyValue, loginData.texturesPropertySignature);
					loginData.texturesPropertyValue = null;
					loginData.texturesPropertySignature = null;
				}
				if (loginData.eaglerPlayerProperty != (byte) 0) {
					injector.injectIsEaglerPlayerProperty(loginData.eaglerPlayerProperty == (byte) 2);
				}
				injector.complete();
			}
		} else {
			pipelineData = data;
		}
		event.registerIntent(plugin);
		awaitPlayState(pipelineData, () -> {
			try {
				plugin.initializePlayer(player, pipelineData, (b) -> {
					if (b) {
						event.completeIntent(plugin);
					} else {
						// Hang forever on cancel, connection is already dead, async callback will GC
					}
				});
			} catch (Exception ex) {
				try {
					event.completeIntent(plugin);
				} catch (IllegalStateException exx) {
					return;
				}
				if (ex instanceof RuntimeException ee)
					throw ee;
				throw new RuntimeException("Uncaught exception", ex);
			}
		});
	}

	private static void awaitPlayState(IPipelineData conn, Runnable cont) {
		if (conn != null) {
			conn.awaitPlayState(cont);
		} else {
			cont.run();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDisconnectedEvent(PlayerDisconnectEvent event) {
		plugin.dropPlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerConnectedEvent(ServerConnectedEvent event) {
		ProxiedPlayer playerIn = event.getPlayer();
		BungeePlayer player = (BungeePlayer) plugin.getPlayer(playerIn);
		if (player != null) {
			player.server = null;
			plugin.handleServerPreConnect(player);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerSwitchEvent(ServerSwitchEvent event) {
		ProxiedPlayer playerIn = event.getPlayer();
		BungeePlayer player = (BungeePlayer) plugin.getPlayer(playerIn);
		if (player != null) {
			ServerInfo info = playerIn.getServer().getInfo();
			IPlatformServer<ProxiedPlayer> server = plugin.registeredServers.get(info.getName());
			if (server == null) {
				server = new BungeeServer(plugin, info, false);
			}
			player.server = server;
			plugin.handleServerPostConnect(player, server);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerDisconnectedEvent(ServerDisconnectEvent event) {
		IPlatformPlayer<ProxiedPlayer> player = plugin.getPlayer(event.getPlayer());
		if (player != null) {
			((BungeePlayer) player).server = null;
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPluginMessageEvent(PluginMessageEvent event) {
		PluginMessageHandler handler = plugin.registeredChannelsMap.get(event.getTag());
		if (handler != null) {
			event.setCancelled(true);
			Connection src = event.getSender();
			Connection dst = event.getReceiver();
			if (handler.backend) {
				IEaglerXServerMessageHandler<ProxiedPlayer> ls = handler.handler;
				if (ls != null && (src instanceof Server) && (dst instanceof ProxiedPlayer dst2)) {
					IPlatformPlayer<ProxiedPlayer> player = plugin.getPlayer(dst2);
					if (player != null) {
						ls.handle(handler.channel, player, event.getData());
					}
				}
			} else {
				IEaglerXServerMessageHandler<ProxiedPlayer> ls = handler.handler;
				if (ls != null && (src instanceof ProxiedPlayer src2) && (dst instanceof Server)) {
					IPlatformPlayer<ProxiedPlayer> player = plugin.getPlayer(src2);
					if (player != null) {
						ls.handle(handler.channel, player, event.getData());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onProxyPingEvent(ProxyPingEvent event) {
		IEaglerXServerPlayerCountHandler count = plugin.playerCountHandler;
		if (count != null) {
			ServerPing.Players players = event.getResponse().getPlayers();
			players.setOnline(count.getPlayerTotal());
			players.setMax(count.getPlayerMax());
		}
	}

}
