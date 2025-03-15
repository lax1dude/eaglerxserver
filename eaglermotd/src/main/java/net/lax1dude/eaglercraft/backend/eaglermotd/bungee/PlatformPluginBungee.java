package net.lax1dude.eaglercraft.backend.eaglermotd.bungee;

import net.lax1dude.eaglercraft.backend.eaglermotd.EaglerMOTD;
import net.lax1dude.eaglercraft.backend.eaglermotd.IEaglerMOTDLogger;
import net.lax1dude.eaglercraft.backend.eaglermotd.IEaglerMOTDPlatform;
import net.lax1dude.eaglercraft.backend.server.api.bungee.EaglerXServerAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class PlatformPluginBungee extends Plugin implements IEaglerMOTDPlatform<ProxiedPlayer> {

	private JavaLogger logger;
	private EaglerMOTD<ProxiedPlayer> protocol;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		protocol = new EaglerMOTD<ProxiedPlayer>(this);
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
