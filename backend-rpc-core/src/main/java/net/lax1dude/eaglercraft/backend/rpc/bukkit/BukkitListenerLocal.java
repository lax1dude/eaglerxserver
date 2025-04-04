package net.lax1dude.eaglercraft.backend.rpc.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftDestroyPlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftInitializePlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftWebViewChannelEvent;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftWebViewMessageEvent;

class BukkitListenerLocal implements Listener {

	private final PlatformPluginBukkit plugin;

	BukkitListenerLocal(PlatformPluginBukkit plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEaglerPlayerInit(EaglercraftInitializePlayerEvent evt) {
		if(plugin.localInitHandler != null) {
			plugin.localInitHandler.accept(evt);
		}
	}

	@EventHandler
	public void onEaglerPlayerDestroy(EaglercraftDestroyPlayerEvent evt) {
		if(plugin.localDestroyHandler != null) {
			plugin.localDestroyHandler.accept(evt);
		}
	}

	@EventHandler
	public void onWebViewChannel(EaglercraftWebViewChannelEvent evt) {
		if(plugin.localWebViewChannelHandler != null) {
			plugin.localWebViewChannelHandler.accept(evt);
		}
	}

	@EventHandler
	public void onWebViewMessage(EaglercraftWebViewMessageEvent evt) {
		if(plugin.localWebViewMessageHandler != null) {
			plugin.localWebViewMessageHandler.accept(evt);
		}
	}

	@EventHandler
	public void onToggleVoice(EaglercraftVoiceChangeEvent evt) {
		if(plugin.localToggleVoiceHandler != null) {
			plugin.localToggleVoiceHandler.accept(evt);
		}
	}

}
