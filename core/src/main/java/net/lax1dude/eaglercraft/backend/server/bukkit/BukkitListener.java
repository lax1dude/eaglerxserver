package net.lax1dude.eaglercraft.backend.server.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;

class BukkitListener implements Listener {

	private final PlatformPluginBukkit plugin;

	BukkitListener(PlatformPluginBukkit plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent evt) {
		Player player = evt.getPlayer();
		Channel channel = BukkitUnsafe.getPlayerChannel(player);
		BukkitConnection conn = channel.attr(PipelineAttributes.<BukkitConnection>connectionData()).get();
		plugin.initializePlayer(player, conn);
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
