package net.lax1dude.eaglercraft.backend.server.bungee;

import com.google.common.base.Throwables;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageHandler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPipelineData;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.bungee.PlatformPluginBungee.PluginMessageHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeListener implements Listener {

	private final PlatformPluginBungee plugin;

	BungeeListener(PlatformPluginBungee plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = 127)
	public void onPreLoginEvent(PreLoginEvent event) {
		PendingConnection conn = event.getConnection();
		Channel channel = BungeeUnsafe.getInitialHandlerChannel(conn);
		IPipelineData pipelineData = channel.attr(PipelineAttributes.<IPipelineData>pipelineData()).getAndSet(null);
		plugin.initializeConnection(conn, pipelineData,
				channel.attr(PipelineAttributes.<BungeeConnection>connectionData())::set);
		
	}

	@EventHandler(priority = -128)
	public void onLoginEvent(LoginEvent event) {
		BungeeConnection conn = BungeeUnsafe.getInitialHandlerChannel(event.getConnection())
				.attr(PipelineAttributes.<BungeeConnection>connectionData()).get();
		if(conn != null && (conn.eaglerPlayerProperty != (byte) 0 || conn.texturesPropertyValue != null)) {
			BungeeUnsafe.PropertyInjector injector = BungeeUnsafe.propertyInjector(event.getConnection());
			if(conn.texturesPropertyValue != null) {
				injector.injectTexturesProperty(conn.texturesPropertyValue,
						conn.texturesPropertySignature);
				conn.texturesPropertyValue = null;
				conn.texturesPropertySignature = null;
			}
			if(conn.eaglerPlayerProperty != (byte) 0) {
				injector.injectIsEaglerPlayerProperty(conn.eaglerPlayerProperty == (byte) 2);
			}
			injector.complete();
		}
	}

	@EventHandler(priority = 127)
	public void onPostLoginEvent(PostLoginEvent event) {
		ProxiedPlayer player = event.getPlayer();
		BungeeConnection conn = BungeeUnsafe.getInitialHandlerChannel(player.getPendingConnection())
				.attr(PipelineAttributes.<BungeeConnection>connectionData()).get();
		event.registerIntent(plugin);
		conn.awaitPlayState(() -> {
			try {
				plugin.initializePlayer(player, conn, () -> {
					event.completeIntent(plugin);
				});
			}catch(Exception ex) {
				try {
					event.completeIntent(plugin);
				}catch(IllegalStateException exx) {
					return;
				}
				Throwables.throwIfUnchecked(ex);
				throw new RuntimeException("Uncaught exception", ex);
			}
		});
	}

	@EventHandler(priority = -128)
	public void onPlayerDisconnectedEvent(PlayerDisconnectEvent event) {
		plugin.dropPlayer(event.getPlayer());
	}

	@EventHandler(priority = 127)
	public void onServerConnectedEvent(ServerConnectedEvent event) {
		IPlatformPlayer<ProxiedPlayer> player = plugin.getPlayer(event.getPlayer());
		if(player != null) {
			ServerInfo info = event.getServer().getInfo();
			IPlatformServer<ProxiedPlayer> server = null;
			if(info != null) {
				server = plugin.registeredServers.get(info.getName());
				if(server == null) {
					server = new BungeeServer(plugin, info, false);
				}
				((BungeePlayer)player).server = server;
			}else {
				((BungeePlayer)player).server = null;
			}
			plugin.handleServerJoin(player, server);
		}
	}

	@EventHandler(priority = -128)
	public void onServerDisconnectedEvent(ServerDisconnectEvent event) {
		IPlatformPlayer<ProxiedPlayer> player = plugin.getPlayer(event.getPlayer());
		if(player != null) {
			((BungeePlayer)player).server = null;
		}
	}

	@EventHandler
	public void onPluginMessageEvent(PluginMessageEvent event) {
		PluginMessageHandler handler = plugin.registeredChannelsMap.get(event.getTag());
		if(handler != null) {
			event.setCancelled(true);
			Connection src = event.getSender();
			Connection dst = event.getReceiver();
			if(handler.backend) {
				IEaglerXServerMessageHandler<ProxiedPlayer> ls = handler.handler;
				if(ls != null && (src instanceof Server) && (dst instanceof ProxiedPlayer dst2)) {
					IPlatformPlayer<ProxiedPlayer> player = plugin.getPlayer(dst2);
					if(player != null) {
						ls.handle(handler.channel, player, event.getData());
					}
				}
			}else {
				IEaglerXServerMessageHandler<ProxiedPlayer> ls = handler.handler;
				if(ls != null && (src instanceof ProxiedPlayer src2) && (dst instanceof Server)) {
					IPlatformPlayer<ProxiedPlayer> player = plugin.getPlayer(src2);
					if(player != null) {
						ls.handle(handler.channel, player, event.getData());
					}
				}
			}
		}
	}

}
