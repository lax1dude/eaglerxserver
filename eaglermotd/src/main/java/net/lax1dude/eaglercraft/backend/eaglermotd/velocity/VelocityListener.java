package net.lax1dude.eaglercraft.backend.eaglermotd.velocity;

import java.util.function.Consumer;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftMOTDEvent;

class VelocityListener {

	private final PlatformPluginVelocity plugin;

	VelocityListener(PlatformPluginVelocity plugin) {
		this.plugin = plugin;
	}

	@Subscribe
	public void onEaglercraftMOTDEvent(EaglercraftMOTDEvent event) {
		Consumer<IEaglercraftMOTDEvent<Player>> consumer = plugin.onMOTDHandler;
		if(consumer != null) {
			consumer.accept(event);
		}
	}

}
