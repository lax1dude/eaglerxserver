package net.lax1dude.eaglercraft.backend.eaglerfilter.bungee;

import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.eaglerfilter.base.EaglerFilter;
import net.lax1dude.eaglercraft.backend.eaglerfilter.base.IEaglerFilterLogger;
import net.lax1dude.eaglercraft.backend.eaglerfilter.base.IEaglerFilterPlatform;
import net.lax1dude.eaglercraft.backend.server.api.bungee.EaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebSocketOpenEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class PlatformPluginBungee extends Plugin implements IEaglerFilterPlatform<ProxiedPlayer, BaseComponent> {

	private JavaLogger logger;
	private EaglerFilter<ProxiedPlayer, BaseComponent> plugin;
	Consumer<IEaglercraftWebSocketOpenEvent<ProxiedPlayer>> onWebSocketOpenHandler;
	Consumer<IEaglercraftClientBrandEvent<ProxiedPlayer, BaseComponent>> onClientBrandHandler;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		plugin = new EaglerFilter<ProxiedPlayer, BaseComponent>(this);
	}

	@Override
	public void onEnable() {
		getProxy().getPluginManager().registerListener(this, new BungeeListener(this));
		plugin.onEnable(EaglerXServerAPI.instance());
	}

	@Override
	public void onDisable() {
		getProxy().getPluginManager().unregisterListeners(this);
		plugin.onDisable(EaglerXServerAPI.instance());
	}

	@Override
	public IEaglerFilterLogger logger() {
		return logger;
	}

	@Override
	public void setOnWebSocketOpen(Consumer<IEaglercraftWebSocketOpenEvent<ProxiedPlayer>> handler) {
		onWebSocketOpenHandler = handler;
	}

	@Override
	public void setOnClientBrand(Consumer<IEaglercraftClientBrandEvent<ProxiedPlayer, BaseComponent>> handler) {
		onClientBrandHandler = handler;
	}

}
