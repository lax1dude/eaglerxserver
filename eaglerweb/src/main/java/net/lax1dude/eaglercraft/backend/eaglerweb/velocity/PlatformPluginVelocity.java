package net.lax1dude.eaglercraft.backend.eaglerweb.velocity;

import java.io.File;
import java.nio.file.Path;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWeb;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.IEaglerWebLogger;
import net.lax1dude.eaglercraft.backend.eaglerweb.base.IEaglerWebPlatform;
import net.lax1dude.eaglercraft.backend.server.api.velocity.EaglerXServerAPI;

@Plugin(
	id = PlatformPluginVelocity.PLUGIN_ID,
	name = PlatformPluginVelocity.PLUGIN_NAME,
	authors = {
		PlatformPluginVelocity.PLUGIN_AUTHOR
	},
	version = PlatformPluginVelocity.PLUGIN_VERSION,
	description = PlatformPluginVelocity.PLUGIN_DESC,
	dependencies = {
		@Dependency(id = EaglerXServerAPI.PLUGIN_ID, optional = false)
	}
)
public class PlatformPluginVelocity implements IEaglerWebPlatform<Player> {

	public static final String PLUGIN_ID = "eaglerweb";
	public static final String PLUGIN_NAME = "EaglerWeb";
	public static final String PLUGIN_AUTHOR = "lax1dude";
	public static final String PLUGIN_VERSION = "1.0.0";
	public static final String PLUGIN_DESC = "Official EaglerWeb plugin for EaglercraftXServer";

	private final ProxyServer proxy;
	private final Logger logger;
	private final File dataDir;
	private final SLF4JLogger rewindLogger;
	private final EaglerWeb<Player> plugin;
	private CommandMeta refreshCommandMeta;
	IHandleRefresh handleRefresh;

	@Inject
	public PlatformPluginVelocity(ProxyServer proxyIn, Logger loggerIn, @DataDirectory Path dataDirIn) {
		proxy = proxyIn;
		logger = loggerIn;
		dataDir = dataDirIn.toFile();
		rewindLogger = new SLF4JLogger(loggerIn);
		plugin = new EaglerWeb<Player>(this);
	}

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent e) {
    	refreshCommandMeta = (new CommandEaglerWeb(this)).register();
    	plugin.onEnable(EaglerXServerAPI.instance());
	}

	@Subscribe
    public void onProxyShutdown(ProxyShutdownEvent e) {
		if(refreshCommandMeta != null) {
			proxy.getCommandManager().unregister(refreshCommandMeta);
			refreshCommandMeta = null;
		}
		plugin.onDisable(EaglerXServerAPI.instance());
	}

	public ProxyServer proxy() {
		return proxy;
	}

	@Override
	public IEaglerWebLogger logger() {
		return rewindLogger;
	}

	@Override
	public String getVersionString() {
		return PLUGIN_NAME + "/" + PLUGIN_VERSION;
	}

	@Override
	public File getDataFolder() {
		return dataDir;
	}

	@Override
	public void setHandleRefresh(IHandleRefresh handleRefresh) {
		this.handleRefresh = handleRefresh;
	}

}
