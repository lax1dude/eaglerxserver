package net.lax1dude.eaglercraft.backend.rpc.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

class BukkitListener implements Listener {

	private final PlatformPluginBukkit plugin;

	BukkitListener(PlatformPluginBukkit plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent evt) {
		plugin.initializePlayer(evt.getPlayer());
	}

	@EventHandler
	public void onPostLogin(PlayerJoinEvent evt) {
		plugin.confirmPlayer(evt.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuitEvent(PlayerQuitEvent evt) {
		plugin.dropPlayer(evt.getPlayer());
	}

}
