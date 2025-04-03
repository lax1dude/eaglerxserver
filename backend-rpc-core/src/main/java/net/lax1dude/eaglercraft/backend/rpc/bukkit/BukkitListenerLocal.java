package net.lax1dude.eaglercraft.backend.rpc.bukkit;

import java.util.function.Consumer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftDestroyPlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftInitializePlayerEvent;

class BukkitListenerLocal implements Listener {

	private final PlatformPluginBukkit plugin;

	BukkitListenerLocal(PlatformPluginBukkit plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEaglerPlayerInit(EaglercraftInitializePlayerEvent evt) {
		((Consumer<Object>)plugin.localInitHandler).accept(evt);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEaglerPlayerInit(EaglercraftDestroyPlayerEvent evt) {
		((Consumer<Object>)plugin.localDestroyHandler).accept(evt);
	}

}
