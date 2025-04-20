package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.util.Collection;
import java.util.function.Consumer;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.collect.Collections2;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;

class BukkitWorld implements IPlatformServer<Player> {

	private final PlatformPluginBukkit plugin;
	private final World world;

	BukkitWorld(PlatformPluginBukkit plugin, World world) {
		this.plugin = plugin;
		this.world = world;
	}

	@Override
	public boolean isEaglerRegistered() {
		return false;
	}

	@Override
	public String getServerConfName() {
		return world.getName();
	}

	@Override
	public Collection<IPlatformPlayer<Player>> getAllPlayers() {
		return Collections2.transform(world.getPlayers(), plugin::getPlayer);
	}

	@Override
	public void forEachPlayer(Consumer<IPlatformPlayer<Player>> callback) {
		world.getPlayers().forEach((player) -> {
			IPlatformPlayer<Player> platformPlayer = plugin.getPlayer(player);
			if(platformPlayer != null) {
				callback.accept(platformPlayer);
			}
		});
	}

}
