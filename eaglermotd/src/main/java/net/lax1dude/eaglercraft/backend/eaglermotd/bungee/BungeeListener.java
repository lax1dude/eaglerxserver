package net.lax1dude.eaglercraft.backend.eaglermotd.bungee;

import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftMOTDEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeListener implements Listener {

	private final PlatformPluginBungee plugin;

	BungeeListener(PlatformPluginBungee plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEaglercraftMOTDEvent(EaglercraftMOTDEvent event) {
		Consumer<IEaglercraftMOTDEvent<ProxiedPlayer>> consumer = plugin.onMOTDHandler;
		if(consumer != null) {
			consumer.accept(event);
		}
	}

}
