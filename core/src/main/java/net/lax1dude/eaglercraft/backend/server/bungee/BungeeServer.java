package net.lax1dude.eaglercraft.backend.server.bungee;

import java.util.Collection;
import java.util.function.Consumer;

import com.google.common.collect.Collections2;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeServer implements IPlatformServer<ProxiedPlayer> {

	private final PlatformPluginBungee plugin;
	private final ServerInfo serverInfo;
	private final boolean registered;

	BungeeServer(PlatformPluginBungee plugin, ServerInfo serverInfo, boolean registered) {
		this.plugin = plugin;
		this.serverInfo = serverInfo;
		this.registered = registered;
	}

	@Override
	public boolean isEaglerRegistered() {
		return registered;
	}

	@Override
	public String getServerConfName() {
		return serverInfo.getName();
	}

	@Override
	public Collection<IPlatformPlayer<ProxiedPlayer>> getAllPlayers() {
		return Collections2.transform(serverInfo.getPlayers(), plugin::getPlayer);
	}

	@Override
	public void forEachPlayer(Consumer<IPlatformPlayer<ProxiedPlayer>> callback) {
		serverInfo.getPlayers().forEach((player) -> {
			IPlatformPlayer<ProxiedPlayer> platformPlayer = plugin.getPlayer(player);
			if(platformPlayer != null) {
				callback.accept(platformPlayer);
			}
		});
	}

}
