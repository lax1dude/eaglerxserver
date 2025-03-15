package net.lax1dude.eaglercraft.backend.eaglermotd.velocity;

import java.nio.file.Path;

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

import net.lax1dude.eaglercraft.backend.eaglermotd.EaglerMOTD;
import net.lax1dude.eaglercraft.backend.eaglermotd.IEaglerMOTDLogger;
import net.lax1dude.eaglercraft.backend.eaglermotd.IEaglerMOTDPlatform;
import net.lax1dude.eaglercraft.backend.server.api.velocity.EaglerXServerAPI;

@Plugin(
	id = PlatformPluginVelocity.PLUGIN_ID,
	name = PlatformPluginVelocity.PLUGIN_NAME,
	authors = {
		"lax1dude"
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
	private final SLF4JLogger rewindLogger;
	private final EaglerMOTD<Player> eaglermotd;

	@Inject
	public PlatformPluginVelocity(ProxyServer proxyIn, Logger loggerIn, @DataDirectory Path dataDirIn) {
		proxy = proxyIn;
		logger = loggerIn;
		rewindLogger = new SLF4JLogger(loggerIn);
		eaglermotd = new EaglerMOTD<Player>(this);
	}

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent e) {
    	eaglermotd.onEnable(EaglerXServerAPI.instance());
	}

	@Subscribe
    public void onProxyShutdown(ProxyShutdownEvent e) {
		eaglermotd.onDisable(EaglerXServerAPI.instance());
	}

	@Override
	public IEaglerMOTDLogger logger() {
		return rewindLogger;
	}

}
