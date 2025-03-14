package net.lax1dude.eaglercraft.backend.rewind_v1_5.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.lax1dude.eaglercraft.backend.rewind_v1_5.IRewindLogger;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.IRewindPlatform;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.RewindPluginProtocol;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.bungee.JavaLogger;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;

public class PlatformPluginBukkit extends JavaPlugin implements IRewindPlatform<Player> {

	private JavaLogger logger;
	private RewindPluginProtocol<Player> protocol;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		protocol = new RewindPluginProtocol<Player>(this);
	}

	@Override
	public void onEnable() {
		EaglerXServerAPI.instance().getEaglerXRewindService().registerLegacyProtocol(protocol);
	}

	@Override
	public void onDisable() {
		EaglerXServerAPI.instance().getEaglerXRewindService().unregisterLegacyProtocol(protocol);
	}

	@Override
	public IRewindLogger logger() {
		return logger;
	}

}
