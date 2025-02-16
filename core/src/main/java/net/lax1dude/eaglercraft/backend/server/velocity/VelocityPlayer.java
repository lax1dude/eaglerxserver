package net.lax1dude.eaglercraft.backend.server.velocity;

import java.util.UUID;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;

class VelocityPlayer implements IPlatformPlayer<Player> {

	private final Player player;
	private final VelocityConnection connection;
	volatile ScheduledTask confirmTask;
	Object attachment;

	VelocityPlayer(Player player, VelocityConnection connection) {
		this.player = player;
		this.connection = connection;
		this.connection.playerInstance = player;
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
	public void disconnect() {
		player.disconnect(VelocityConnection.DEFAULT_KICK_MESSAGE);
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		player.disconnect((Component)kickMessage);
	}

	@Override
	public <T> T getPlayerAttachment() {
		return (T) attachment;
	}

}
