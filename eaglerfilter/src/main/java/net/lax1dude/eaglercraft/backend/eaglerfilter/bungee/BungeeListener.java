package net.lax1dude.eaglercraft.backend.eaglerfilter.bungee;

import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftWebSocketOpenEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebSocketOpenEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

class BungeeListener implements Listener {

	private final PlatformPluginBungee plugin;

	BungeeListener(PlatformPluginBungee plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEaglercraftWebSocketOpenEvent(EaglercraftWebSocketOpenEvent event) {
		Consumer<IEaglercraftWebSocketOpenEvent<ProxiedPlayer>> consumer = plugin.onWebSocketOpenHandler;
		if(consumer != null) {
			consumer.accept(event);
		}
	}

	@EventHandler
	public void onEaglercraftMOTDEvent(EaglercraftClientBrandEvent event) {
		Consumer<IEaglercraftClientBrandEvent<ProxiedPlayer, BaseComponent>> consumer = plugin.onClientBrandHandler;
		if(consumer != null) {
			consumer.accept(event);
		}
	}

}
