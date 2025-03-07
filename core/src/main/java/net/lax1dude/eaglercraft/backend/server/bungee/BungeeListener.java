package net.lax1dude.eaglercraft.backend.server.bungee;

import com.google.common.base.Throwables;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageHandler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.bungee.PlatformPluginBungee.PluginMessageHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

class BungeeListener implements Listener {

	private final PlatformPluginBungee plugin;

	BungeeListener(PlatformPluginBungee plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = 127)
	public void onPreLogin(PreLoginEvent event) {
		PendingConnection conn = event.getConnection();
		Channel channel = BungeeUnsafe.getInitialHandlerChannel(conn);
		Object pipelineData = channel.attr(PipelineAttributes.<Object>pipelineData()).getAndSet(null);
		plugin.initializeConnection(conn, pipelineData,
				channel.attr(PipelineAttributes.<BungeeConnection>connectionData())::set);
	}

	@EventHandler(priority = 127)
	public void onPostLogin(PostLoginEvent event) {
		ProxiedPlayer player = event.getPlayer();
		BungeeConnection conn = BungeeUnsafe.getInitialHandlerChannel(player.getPendingConnection())
				.attr(PipelineAttributes.<BungeeConnection>connectionData()).get();
		event.registerIntent(plugin);
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
	}

	@EventHandler(priority = -128)
	public void onPlayerDisconnected(PlayerDisconnectEvent event) {
		plugin.dropPlayer(event.getPlayer());
	}

	@EventHandler(priority = 127)
	public void onServerConnected(ServerConnectedEvent event) {
		ServerInfo info = event.getServer().getInfo();
		if(info != null) {
			IPlatformServer<ProxiedPlayer> server = plugin.registeredServers.get(info.getName());
			if(server == null) {
				server = new BungeeServer(plugin, info, false);
			}
			IPlatformPlayer<ProxiedPlayer> player = plugin.getPlayer(event.getPlayer());
			if(player != null) {
				((BungeePlayer)player).server = server;
			}
		}else {
			IPlatformPlayer<ProxiedPlayer> player = plugin.getPlayer(event.getPlayer());
			if(player != null) {
				((BungeePlayer)player).server = null;
			}
		}
	}

	@EventHandler(priority = -128)
	public void onServerDisconnected(ServerDisconnectEvent event) {
		((BungeePlayer)plugin.getPlayer(event.getPlayer())).server = null;
	}

	@EventHandler
	public void onPluginMessage(PluginMessageEvent event) {
		PluginMessageHandler handler = plugin.registeredChannelsMap.get(event.getTag());
		if(handler != null) {
			event.setCancelled(true);
			Connection src = event.getSender();
			Connection dst = event.getReceiver();
			if(handler.backend) {
				IEaglerXServerMessageHandler<ProxiedPlayer> ls = handler.handler;
				if(ls != null && (src instanceof Server) && (dst instanceof ProxiedPlayer)) {
					IPlatformPlayer<ProxiedPlayer> player = plugin.getPlayer((ProxiedPlayer)dst);
					if(player != null) {
						ls.handle(handler.channel, player, event.getData());
					}
				}
			}else {
				IEaglerXServerMessageHandler<ProxiedPlayer> ls = handler.handler;
				if(ls != null && (src instanceof ProxiedPlayer) && (dst instanceof Server)) {
					IPlatformPlayer<ProxiedPlayer> player = plugin.getPlayer((ProxiedPlayer)src);
					if(player != null) {
						ls.handle(handler.channel, player, event.getData());
					}
				}
			}
		}
	}

}
