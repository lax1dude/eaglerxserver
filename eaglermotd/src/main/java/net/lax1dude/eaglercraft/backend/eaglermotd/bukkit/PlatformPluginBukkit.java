package net.lax1dude.eaglercraft.backend.eaglermotd.bukkit;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.lax1dude.eaglercraft.backend.eaglermotd.base.EaglerMOTD;
import net.lax1dude.eaglercraft.backend.eaglermotd.base.IEaglerMOTDLogger;
import net.lax1dude.eaglercraft.backend.eaglermotd.base.IEaglerMOTDPlatform;
import net.lax1dude.eaglercraft.backend.eaglermotd.bungee.JavaLogger;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;

public class PlatformPluginBukkit extends JavaPlugin implements IEaglerMOTDPlatform<Player> {

	private JavaLogger logger;
	private EaglerMOTD<Player> eaglermotd;
	Consumer<IEaglercraftMOTDEvent<Player>> onMOTDHandler;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		eaglermotd = new EaglerMOTD<Player>(this);
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new BukkitListener(this), this);
		eaglermotd.onEnable(EaglerXServerAPI.instance());
	}

	@Override
	public void onDisable() {
		eaglermotd.onDisable(EaglerXServerAPI.instance());
	}

	@Override
	public IEaglerMOTDLogger logger() {
		return logger;
	}

	@Override
	public void setOnMOTD(Consumer<IEaglercraftMOTDEvent<Player>> handler) {
		this.onMOTDHandler = handler;
	}

}
