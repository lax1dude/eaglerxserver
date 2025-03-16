package net.lax1dude.eaglercraft.backend.eaglerfilter.bukkit;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.lax1dude.eaglercraft.backend.eaglerfilter.base.EaglerFilter;
import net.lax1dude.eaglercraft.backend.eaglerfilter.base.IEaglerFilterLogger;
import net.lax1dude.eaglercraft.backend.eaglerfilter.base.IEaglerFilterPlatform;
import net.lax1dude.eaglercraft.backend.eaglerfilter.bungee.JavaLogger;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebSocketOpenEvent;
import net.md_5.bungee.api.chat.BaseComponent;

public class PlatformPluginBukkit extends JavaPlugin implements IEaglerFilterPlatform<Player, BaseComponent> {

	private JavaLogger logger;
	private EaglerFilter<Player, BaseComponent> plugin;
	Consumer<IEaglercraftWebSocketOpenEvent<Player>> onWebSocketOpenHandler;
	Consumer<IEaglercraftClientBrandEvent<Player, BaseComponent>> onClientBrandHandler;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		plugin = new EaglerFilter<Player, BaseComponent>(this);
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new BukkitListener(this), this);
		plugin.onEnable(EaglerXServerAPI.instance());
	}

	@Override
	public void onDisable() {
		plugin.onDisable(EaglerXServerAPI.instance());
	}

	@Override
	public IEaglerFilterLogger logger() {
		return logger;
	}

	@Override
	public void setOnWebSocketOpen(Consumer<IEaglercraftWebSocketOpenEvent<Player>> handler) {
		onWebSocketOpenHandler = handler;
	}

	@Override
	public void setOnClientBrand(Consumer<IEaglercraftClientBrandEvent<Player, BaseComponent>> handler) {
		onClientBrandHandler = handler;
	}

}
