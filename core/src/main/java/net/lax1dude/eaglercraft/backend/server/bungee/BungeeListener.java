package net.lax1dude.eaglercraft.backend.server.bungee;

import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

class BungeeListener implements Listener {

	private final PlatformPluginBungee plugin;

	BungeeListener(PlatformPluginBungee plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = 127)
	public void onPlayerLogin(PostLoginEvent event) {
		ProxiedPlayer player = event.getPlayer();
		BungeeConnection conn = BungeeUnsafe.getInitialHandlerChannel(player.getPendingConnection())
				.attr(PipelineAttributes.<BungeeConnection>connectionData()).get();
		plugin.initializePlayer(player, conn);
	}

	@EventHandler(priority = -128)
	public void onPlayerDisconnected(PlayerDisconnectEvent event) {
		plugin.dropPlayer(event.getPlayer());
	}

}
