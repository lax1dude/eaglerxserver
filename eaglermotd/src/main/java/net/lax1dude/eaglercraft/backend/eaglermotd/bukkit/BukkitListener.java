package net.lax1dude.eaglercraft.backend.eaglermotd.bukkit;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftMOTDEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;

public class BukkitListener implements Listener {

	private final PlatformPluginBukkit plugin;

	BukkitListener(PlatformPluginBukkit plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEaglercraftMOTDEvent(EaglercraftMOTDEvent event) {
		Consumer<IEaglercraftMOTDEvent<Player>> consumer = plugin.onMOTDHandler;
		if(consumer != null) {
			consumer.accept(event);
		}
	}

}
