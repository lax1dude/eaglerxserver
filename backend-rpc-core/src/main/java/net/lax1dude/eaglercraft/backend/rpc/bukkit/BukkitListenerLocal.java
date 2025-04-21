package net.lax1dude.eaglercraft.backend.rpc.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
	public void onInitializePlayer(EaglercraftInitializePlayerEvent evt) {
		if(plugin.initializePlayerHandler != null) {
			plugin.initializePlayerHandler.accept(evt);
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
		if(plugin.localVoiceChangeHandler != null) {
			plugin.localVoiceChangeHandler.accept(evt);
		}
	}

}
