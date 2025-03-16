package net.lax1dude.eaglercraft.backend.eaglerweb.bungee;

import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWeb;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.IEaglerWebLogger;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.IEaglerWebPlatform;
import net.lax1dude.eaglercraft.backend.server.api.bungee.EaglerXServerAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class PlatformPluginBungee extends Plugin implements IEaglerWebPlatform<ProxiedPlayer> {

	private JavaLogger logger;
	private EaglerWeb<ProxiedPlayer> plugin;
	IHandleRefresh handleRefresh;

	@Override
	public void onLoad() {
		logger = new JavaLogger(getLogger());
		plugin = new EaglerWeb<ProxiedPlayer>(this);
	}

	@Override
	public void onEnable() {
		getProxy().getPluginManager().registerCommand(this, new CommandEaglerWeb(this));
		plugin.onEnable(EaglerXServerAPI.instance());
	}

	@Override
	public void onDisable() {
		getProxy().getPluginManager().unregisterCommands(this);
		plugin.onDisable(EaglerXServerAPI.instance());
	}

	@Override
	public IEaglerWebLogger logger() {
		return logger;
	}

	@Override
	public void setHandleRefresh(IHandleRefresh handleRefresh) {
		this.handleRefresh = handleRefresh;
	}

}
