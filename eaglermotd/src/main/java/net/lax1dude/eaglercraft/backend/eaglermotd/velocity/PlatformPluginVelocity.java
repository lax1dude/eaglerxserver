package net.lax1dude.eaglercraft.backend.eaglermotd.velocity;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import net.lax1dude.eaglercraft.backend.eaglermotd.base.EaglerMOTD;
import net.lax1dude.eaglercraft.backend.eaglermotd.base.IEaglerMOTDLogger;
import net.lax1dude.eaglercraft.backend.eaglermotd.base.IEaglerMOTDPlatform;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;
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
public class PlatformPluginVelocity implements IEaglerMOTDPlatform<Player> {

	public static final String PLUGIN_ID = "eaglermotd-reborn";
	public static final String PLUGIN_NAME = "EaglerMOTD-Reborn";
	public static final String PLUGIN_AUTHOR = "lax1dude";
	public static final String PLUGIN_VERSION = "1.0.0";
	public static final String PLUGIN_DESC = "Official EaglerMOTD plugin for EaglercraftXServer";

	private final ProxyServer proxy;
	private final Logger logger;
	private final File dataDir;
	private final SLF4JLogger rewindLogger;
	private final EaglerMOTD<Player> eaglermotd;
	Consumer<IEaglercraftMOTDEvent<Player>> onMOTDHandler;

	@Inject
	public PlatformPluginVelocity(ProxyServer proxyIn, Logger loggerIn, @DataDirectory Path dataDirIn) {
		proxy = proxyIn;
		logger = loggerIn;
		dataDir = dataDirIn.toFile();
		rewindLogger = new SLF4JLogger(loggerIn);
		eaglermotd = new EaglerMOTD<Player>(this);
	}

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent e) {
    	proxy.getEventManager().register(this, new VelocityListener(this));
    	eaglermotd.onEnable(EaglerXServerAPI.instance());
	}

	@Subscribe
    public void onProxyShutdown(ProxyShutdownEvent e) {
		proxy.getEventManager().unregisterListeners(this);
		eaglermotd.onDisable(EaglerXServerAPI.instance());
	}

	@Override
	public IEaglerMOTDLogger logger() {
		return rewindLogger;
	}

	@Override
	public void setOnMOTD(Consumer<IEaglercraftMOTDEvent<Player>> handler) {
		this.onMOTDHandler = handler;
	}

	@Override
	public File getDataFolder() {
		return dataDir;
	}

}
