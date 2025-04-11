package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.util.concurrent.CountDownLatch;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.md_5.bungee.api.chat.BaseComponent;

class BukkitListener implements Listener {

	private final PlatformPluginBukkit plugin;

	BukkitListener(PlatformPluginBukkit plugin) {
		this.plugin = plugin;
	}

	private volatile boolean thisIsSafe = false;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent evt) {
		Player player = evt.getPlayer();
		Channel channel = BukkitUnsafe.getPlayerChannel(player);
		Attribute<BukkitConnection> attr = channel.attr(PipelineAttributes.<BukkitConnection>connectionData());
		CountDownLatch latch = new CountDownLatch(1);
		thisIsSafe = false;
		plugin.initializePlayer(player, attr.get(), attr::set, (res) -> {
			thisIsSafe = true;
			if(res.closed) {
				evt.disallow(PlayerLoginEvent.Result.KICK_OTHER,
						res.msg != null ? ((BaseComponent) res.msg).toLegacyText() : "Connection Closed");
			}
			latch.countDown();
		});
		if(!thisIsSafe) {
			long now = System.nanoTime();
			plugin.logger().warn("PlayerLoginEvent is being blocked by EaglerXServer or a "
					+ "dependant plugin, this will stall the server's main thread and will cause "
					+ "a deadlock if a dependent plugin attempts to await a non-async task!!!");
			try {
				// FUCK! FUCK! FUCK!
				latch.await();
			}catch(InterruptedException ex) {
				plugin.logger().warn("Server thread interrupted");
			}
			plugin.logger().warn("Server thread is resuming! (Stalled for "
					+ ((System.nanoTime() - now) / (50l * 1000000l)) + " ticks)");
		}
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
