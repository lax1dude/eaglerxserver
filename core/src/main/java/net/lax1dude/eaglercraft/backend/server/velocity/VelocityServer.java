package net.lax1dude.eaglercraft.backend.server.velocity;

import java.util.Collection;
import java.util.function.Consumer;

import com.google.common.collect.Collections2;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;

class VelocityServer implements IPlatformServer<Player> {

	private final PlatformPluginVelocity plugin;
	private final RegisteredServer server;
	private final boolean registered;

	public VelocityServer(PlatformPluginVelocity plugin, RegisteredServer server, boolean registered) {
		this.plugin = plugin;
		this.server = server;
		this.registered = registered;
	}

	@Override
	public boolean isEaglerRegistered() {
		return registered;
	}

	@Override
	public String getServerConfName() {
		return server.getServerInfo().getName();
	}

	@Override
	public Collection<IPlatformPlayer<Player>> getAllPlayers() {
		return Collections2.transform(server.getPlayersConnected(), plugin::getPlayer);
	}

	@Override
	public void forEachPlayer(Consumer<IPlatformPlayer<Player>> callback) {
		server.getPlayersConnected().forEach((player) -> {
			IPlatformPlayer<Player> platformPlayer = plugin.getPlayer(player);
			if(platformPlayer != null) {
				callback.accept(platformPlayer);
			}
		});
	}

}
