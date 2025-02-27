package net.lax1dude.eaglercraft.backend.rewind_v1_5.velocity;

import java.nio.file.Path;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import net.lax1dude.eaglercraft.backend.rewind_v1_5.IRewindPlatform;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.RewindPluginProtocol;
import net.lax1dude.eaglercraft.backend.server.api.velocity.EaglerXServerAPI;

public class PlatformPluginVelocity implements IRewindPlatform<Player> {

	private final ProxyServer proxy;
	private final Logger logger;
	private final RewindPluginProtocol<Player> protocol;

	@Inject
	public PlatformPluginVelocity(ProxyServer proxyIn, Logger loggerIn, @DataDirectory Path dataDirIn) {
		proxy = proxyIn;
		logger = loggerIn;
		protocol = new RewindPluginProtocol<Player>(this);
	}

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent e) {
		EaglerXServerAPI.instance().getEaglerXRewindService().registerLegacyProtocol(protocol);
	}

	@Subscribe
    public void onProxyShutdown(ProxyShutdownEvent e) {
		EaglerXServerAPI.instance().getEaglerXRewindService().unregisterLegacyProtocol(protocol);
	}

}
