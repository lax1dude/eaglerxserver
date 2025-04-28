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

package net.lax1dude.eaglercraft.backend.server.velocity;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent.ForwardResult;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ListenerBoundEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.query.ProxyQueryEvent;
import com.velocitypowered.api.network.ListenerType;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelMessageSink;
import com.velocitypowered.api.proxy.messages.ChannelMessageSource;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.api.util.GameProfile.Property;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerPlayerCountHandler;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageHandler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineData;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.velocity.PlatformPluginVelocity.PluginMessageHandler;

class VelocityListener {

	private final PlatformPluginVelocity plugin;

	private static final Property isEaglerPlayerT = new Property("isEaglerPlayer", "true", "");
	private static final Property isEaglerPlayerF = new Property("isEaglerPlayer", "false", "");
	private static final Predicate<Property> isEaglerPlayerPredicate = (prop) -> "isEaglerPlayer"
			.equals(prop.getName());
	private static final Predicate<Property> texturesPredicate = (prop) -> "textures".equals(prop.getName());

	VelocityListener(PlatformPluginVelocity plugin) {
		this.plugin = plugin;
	}

	@Subscribe(async = false)
	public void onListenerBound(ListenerBoundEvent bindEvent) {
		if (bindEvent.getListenerType() == ListenerType.MINECRAFT) {
			VelocityUnsafe.injectListenerAttr(plugin.proxy(), bindEvent.getAddress(), plugin.listenersToInit);
		}
	}

	@Subscribe(priority = Short.MAX_VALUE)
	public void onPreLoginEvent(PreLoginEvent handshakeEvent) {
		InboundConnection conn = handshakeEvent.getConnection();
		Channel channel = VelocityUnsafe.getInboundChannel(conn);
		IPipelineData pipelineData = channel.attr(PipelineAttributes.<IPipelineData>pipelineData()).getAndSet(null);
		plugin.initializeConnection(conn, handshakeEvent.getUsername(), handshakeEvent.getUniqueId(), pipelineData,
				channel.attr(PipelineAttributes.<VelocityConnection>connectionData())::set);
	}

	@Subscribe(priority = -16384)
	public void onGameProfileRequestEvent(GameProfileRequestEvent gameProfileEvent) {
		VelocityConnection conn = VelocityUnsafe.getInboundChannel(gameProfileEvent.getConnection())
				.attr(PipelineAttributes.<VelocityConnection>connectionData()).get();
		GameProfile gameProfile = gameProfileEvent.getGameProfile();
		boolean changed = false;
		if (conn.uuid != null && !conn.uuid.equals(gameProfile.getId())) {
			gameProfile = gameProfile.withId(conn.uuid);
			changed = true;
		}
		if (conn.texturesPropertyValue != null || conn.eaglerPlayerProperty != (byte) 0) {
			List<GameProfile.Property> props = gameProfile.getProperties();
			List<GameProfile.Property> fixedProps = new LinkedList<>(props);
			if (conn.texturesPropertyValue != null) {
				fixedProps.removeIf(texturesPredicate);
				fixedProps.add(new Property("textures", conn.texturesPropertyValue, conn.texturesPropertySignature));
				conn.texturesPropertyValue = null;
				conn.texturesPropertySignature = null;
			}
			if (conn.eaglerPlayerProperty != (byte) 0) {
				fixedProps.removeIf(isEaglerPlayerPredicate);
				fixedProps.add(conn.eaglerPlayerProperty == (byte) 2 ? isEaglerPlayerT : isEaglerPlayerF);
			}
			gameProfile = gameProfile.withProperties(fixedProps);
			changed = true;
		}
		if (changed) {
			gameProfileEvent.setGameProfile(gameProfile);
		}
	}

	@Subscribe(async = false)
	public void onPermissionsSetupEvent(PermissionsSetupEvent permissionsSetupEvent) {
		// Fired right before compression is enabled
		PermissionSubject p = permissionsSetupEvent.getSubject();
		if (p instanceof Player player) {
			VelocityConnection conn = VelocityUnsafe.getInboundChannel(player)
					.attr(PipelineAttributes.<VelocityConnection>connectionData()).get();
			if (conn.compressionDisable) {
				VelocityUnsafe.injectCompressionDisable(plugin.proxy(), player);
			}
		}
	}

	@Subscribe(priority = Short.MAX_VALUE, async = true)
	public void onPostLoginEvent(PostLoginEvent loginEvent, Continuation cont) {
		Player player = loginEvent.getPlayer();
		VelocityConnection conn = VelocityUnsafe.getInboundChannel(player)
				.attr(PipelineAttributes.<VelocityConnection>connectionData()).get();
		conn.awaitPlayState(() -> {
			try {
				plugin.initializePlayer(player, conn, (b) -> {
					if (b) {
						cont.resume();
					} else {
						// Hang forever on cancel, connection is already dead, async callback will GC
					}
				});
			} catch (Exception ex) {
				cont.resumeWithException(ex);
			}
		});
	}

	@Subscribe(priority = Short.MIN_VALUE)
	public void onPlayerDisconnected(DisconnectEvent disconnectEvent) {
		plugin.dropPlayer(disconnectEvent.getPlayer());
	}

	@Subscribe(priority = Short.MIN_VALUE, async = false)
	public void onServerPreConnected(ServerPreConnectEvent connectEvent) {
		if (connectEvent.getResult().isAllowed()) {
			IPlatformPlayer<Player> platformPlayer = plugin.getPlayer(connectEvent.getPlayer());
			if (platformPlayer != null) {
				((VelocityPlayer) platformPlayer).server = null;
				plugin.handleServerPreConnect(platformPlayer);
			}
		}
	}

	@Subscribe(priority = Short.MAX_VALUE, async = false)
	public void onServerPostConnected(ServerPostConnectEvent connectEvent) {
		Optional<ServerConnection> serverCon = connectEvent.getPlayer().getCurrentServer();
		if (serverCon.isPresent()) {
			RegisteredServer server = serverCon.get().getServer();
			IPlatformPlayer<Player> platformPlayer = plugin.getPlayer(connectEvent.getPlayer());
			if (platformPlayer != null) {
				IPlatformServer<Player> platformServer = null;
				platformServer = plugin.getRegisteredServers().get(server.getServerInfo().getName());
				if (platformServer == null) {
					platformServer = new VelocityServer(plugin, server, false);
				}
				((VelocityPlayer) platformPlayer).server = platformServer;
				plugin.handleServerPostConnect(platformPlayer, platformServer);
			}
		}
	}

	@Subscribe(priority = Short.MAX_VALUE, async = false)
	public void onPluginMessageEvent(PluginMessageEvent evt) {
		PluginMessageHandler handler = plugin.registeredChannelsMap.get(evt.getIdentifier());
		if (handler != null) {
			evt.setResult(ForwardResult.handled());
			ChannelMessageSource src = evt.getSource();
			ChannelMessageSink dst = evt.getTarget();
			if (handler.backend) {
				IEaglerXServerMessageHandler<Player> ls = handler.handler;
				if (ls != null && (src instanceof ServerConnection) && (dst instanceof Player dst2)) {
					IPlatformPlayer<Player> player = plugin.getPlayer(dst2);
					if (player != null) {
						ls.handle(handler.channel, player, evt.getData());
					}
				}
			} else {
				IEaglerXServerMessageHandler<Player> ls = handler.handler;
				if (ls != null && (src instanceof Player src2) && (dst instanceof ServerConnection)) {
					IPlatformPlayer<Player> player = plugin.getPlayer(src2);
					if (player != null) {
						ls.handle(handler.channel, player, evt.getData());
					}
				}
			}
		}
	}

	@Subscribe(priority = 16384, async = false)
	public void onProxyPingEvent(ProxyPingEvent evt) {
		IEaglerXServerPlayerCountHandler count = plugin.playerCountHandler;
		if (count != null) {
			evt.setPing(evt.getPing().asBuilder().onlinePlayers(count.getPlayerTotal())
					.maximumPlayers(count.getPlayerMax()).build());
		}
	}

	@Subscribe(priority = 16384, async = false)
	public void onProxyQueryEvent(ProxyQueryEvent evt) {
		IEaglerXServerPlayerCountHandler count = plugin.playerCountHandler;
		if (count != null) {
			evt.setResponse(evt.getResponse().toBuilder().currentPlayers(count.getPlayerTotal())
					.maxPlayers(count.getPlayerMax()).build());
		}
	}

}
