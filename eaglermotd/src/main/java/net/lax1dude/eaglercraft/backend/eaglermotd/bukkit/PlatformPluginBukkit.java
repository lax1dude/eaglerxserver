package net.lax1dude.eaglercraft.backend.eaglermotd.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.lax1dude.eaglercraft.backend.eaglermotd.EaglerMOTD;
import net.lax1dude.eaglercraft.backend.eaglermotd.IEaglerMOTDLogger;
import net.lax1dude.eaglercraft.backend.eaglermotd.IEaglerMOTDPlatform;
import net.lax1dude.eaglercraft.backend.eaglermotd.bungee.JavaLogger;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;

public class PlatformPluginBukkit extends JavaPlugin implements IEaglerMOTDPlatform<Player> {

	private JavaLogger logger;
	private EaglerMOTD<Player> protocol;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		protocol = new EaglerMOTD<Player>(this);
	}

	@Override
	public void onEnable() {
		protocol.onEnable(EaglerXServerAPI.instance());
	}

	@Override
	public void onDisable() {
		protocol.onDisable(EaglerXServerAPI.instance());
	}

	@Override
	public IEaglerMOTDLogger logger() {
		return logger;
	}

}
