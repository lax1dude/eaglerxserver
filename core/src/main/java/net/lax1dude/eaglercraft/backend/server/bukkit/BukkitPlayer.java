package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.md_5.bungee.api.chat.BaseComponent;

class BukkitPlayer implements IPlatformPlayer<Player> {

	private final Player player;
	private final BukkitConnection connection;
	volatile BukkitTask confirmTask;
	Object attachment;

	BukkitPlayer(Player player, BukkitConnection connection) {
		this.player = player;
		this.connection = connection;
		this.connection.bindPlayer(player);
	}

	@Override
	public IPlatformConnection getConnection() {
		return connection;
	}

	@Override
	public Player getPlayerObject() {
		return player;
	}

	@Override
	public String getUsername() {
		return player.getName();
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public boolean isConnected() {
		Channel c = BukkitUnsafe.getPlayerChannel(player);
		return c != null && c.isActive();
	}

	@Override
	public boolean isOnlineMode() {
		return connection.isOnlineMode();
	}

	@Override
	public String getMinecraftBrand() {
		return "vanilla"; // TODO how to get brand?
	}

	@Override
	public void disconnect() {
		player.kickPlayer("Connection Closed");
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		player.kickPlayer(((BaseComponent)kickMessage).toLegacyText());
	}

	@Override
	public <T> T getPlayerAttachment() {
		return (T) attachment;
	}

}
