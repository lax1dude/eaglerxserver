package net.lax1dude.eaglercraft.backend.rewind_v1_5.bungee;

import net.lax1dude.eaglercraft.backend.rewind_v1_5.IRewindLogger;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.IRewindPlatform;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.RewindPluginProtocol;
import net.lax1dude.eaglercraft.backend.server.api.bungee.EaglerXServerAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class PlatformPluginBungee extends Plugin implements IRewindPlatform<ProxiedPlayer> {

	private JavaLogger logger;
	private RewindPluginProtocol<ProxiedPlayer> protocol;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		protocol = new RewindPluginProtocol<ProxiedPlayer>(this);
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
