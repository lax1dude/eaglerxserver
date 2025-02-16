package net.lax1dude.eaglercraft.backend.server.velocity;

import java.util.UUID;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;

class VelocityPlayer implements IPlatformPlayer<Player> {

	private final Player player;
	private final VelocityConnection connection;
	Object attachment;

	VelocityPlayer(Player player, VelocityConnection connection) {
		this.player = player;
		this.connection = connection;
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
		return player.getUsername();
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public boolean isConnected() {
		return player.isActive();
	}

	@Override
	public boolean isOnlineMode() {
		return player.isOnlineMode();
	}

	@Override
	public String getMinecraftBrand() {
		return player.getClientBrand();
	}

	@Override
	public <T> T getPlayerAttachment() {
		return (T) attachment;
	}

}
