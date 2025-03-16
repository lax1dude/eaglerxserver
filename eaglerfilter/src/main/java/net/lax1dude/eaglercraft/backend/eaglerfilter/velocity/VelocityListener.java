package net.lax1dude.eaglercraft.backend.eaglerfilter.velocity;

import java.util.function.Consumer;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebSocketOpenEvent;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftWebSocketOpenEvent;

class VelocityListener {

	private final PlatformPluginVelocity plugin;

	VelocityListener(PlatformPluginVelocity plugin) {
		this.plugin = plugin;
	}

	@Subscribe
	public void onEaglercraftWebSocketOpenEvent(EaglercraftWebSocketOpenEvent event) {
		Consumer<IEaglercraftWebSocketOpenEvent<Player>> consumer = plugin.onWebSocketOpenHandler;
		if(consumer != null) {
			consumer.accept(event);
		}
	}

	@Subscribe
	public void onEaglercraftClientBrandEvent(EaglercraftClientBrandEvent event) {
		Consumer<IEaglercraftClientBrandEvent<Player, Component>> consumer = plugin.onClientBrandHandler;
		if(consumer != null) {
			consumer.accept(event);
		}
	}

}
