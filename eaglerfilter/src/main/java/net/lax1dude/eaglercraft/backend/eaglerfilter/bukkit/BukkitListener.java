package net.lax1dude.eaglercraft.backend.eaglerfilter.bukkit;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftWebSocketOpenEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebSocketOpenEvent;
import net.md_5.bungee.api.chat.BaseComponent;

class BukkitListener implements Listener {

	private final PlatformPluginBukkit plugin;

	BukkitListener(PlatformPluginBukkit plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEaglercraftWebSocketOpenEvent(EaglercraftWebSocketOpenEvent event) {
		Consumer<IEaglercraftWebSocketOpenEvent<Player>> consumer = plugin.onWebSocketOpenHandler;
		if(consumer != null) {
			consumer.accept(event);
		}
	}

	@EventHandler
	public void onEaglercraftClientBrandEvent(EaglercraftClientBrandEvent event) {
		Consumer<IEaglercraftClientBrandEvent<Player, BaseComponent>> consumer = plugin.onClientBrandHandler;
		if(consumer != null) {
			consumer.accept(event);
		}
	}

}
