package net.lax1dude.eaglercraft.backend.server.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.base.Throwables;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import net.lax1dude.eaglercraft.backend.server.adapter.PipelineAttributes;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.PlayerLoginInitEvent;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.PlayerLoginPostEvent;

class BukkitListener implements Listener {

	private final PlatformPluginBukkit plugin;

	BukkitListener(PlatformPluginBukkit plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLoginEvent(PlayerLoginEvent evt) {
		plugin.postLoginInjector.handleLoginEvent(evt);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerLoginInitEvent(PlayerLoginInitEvent evt) {
		plugin.handleConnectionInit(evt.netty().getChannel());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerPostLoginEvent(PlayerLoginPostEvent evt) {
		Player player = evt.getPlayer();
		Channel channel = evt.netty().getChannel();
		Attribute<BukkitConnection> attr = channel.attr(PipelineAttributes.<BukkitConnection>connectionData());
		BukkitConnection conn = attr.get();
		evt.registerIntent(plugin);
		Runnable cont = () -> {
			try {
				plugin.initializePlayer(player, conn, attr::set, (b) -> {
					if(b) {
						evt.completeIntent(plugin);
					}else {
						// Hang forever on cancel, connection is already dead, async callback will GC
					}
				});
			}catch(Exception ex) {
				try {
					evt.completeIntent(plugin);
				}catch(IllegalStateException exx) {
					return;
				}
				Throwables.throwIfUnchecked(ex);
				throw new RuntimeException("Uncaught exception", ex);
			}
		};
		if(conn != null) {
			conn.awaitPlayState(cont);
		}else {
			cont.run();
		}
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent evt) {
		plugin.confirmPlayer(evt.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuitEvent(PlayerQuitEvent evt) {
		plugin.dropPlayer(evt.getPlayer());
	}

}
