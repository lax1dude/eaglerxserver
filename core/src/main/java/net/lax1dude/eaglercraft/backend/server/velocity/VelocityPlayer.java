package net.lax1dude.eaglercraft.backend.server.velocity;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Optional;
import java.util.UUID;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.util.GameProfile;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;

class VelocityPlayer implements IPlatformPlayer<Player> {

	private static final VarHandle CONFIRM_TASK_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			CONFIRM_TASK_HANDLE = l.findVarHandle(VelocityPlayer.class, "confirmTask", ScheduledTask.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final Player player;
	private final VelocityConnection connection;
	volatile ScheduledTask confirmTask;
	Object attachment;
	IPlatformServer<Player> server;

	VelocityPlayer(Player player, VelocityConnection connection) {
		this.player = player;
		this.connection = connection;
		this.connection.playerInstance = player;
	}

	ScheduledTask xchgConfirmTask() {
		return (ScheduledTask)CONFIRM_TASK_HANDLE.getAndSetAcquire(this, null);
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
	public IPlatformServer<Player> getServer() {
		return server;
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
	public void sendDataClient(String channel, byte[] message) {
		VelocityUnsafe.sendDataClient(player, channel, message);
	}

	@Override
	public void sendDataBackend(String channel, byte[] message) {
		Optional<ServerConnection> serverCon = player.getCurrentServer();
		if(serverCon.isPresent()) {
			VelocityUnsafe.sendDataBackend(serverCon.get(), channel, message);
		}
	}

	@Override
	public String getTexturesProperty() {
		GameProfile profile = player.getGameProfile();
		if(profile != null) {
			for(GameProfile.Property prop : profile.getProperties()) {
				if("textures".equals(prop.getName())) {
					return prop.getValue();
				}
			}
		}
		return null;
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
	@SuppressWarnings("unchecked")
	public <T> T getPlayerAttachment() {
		return (T) attachment;
	}

	@Override
	public boolean checkPermission(String permission) {
		return player.hasPermission(permission);
	}

	@Override
	public <ComponentObject> void sendMessage(ComponentObject component) {
		player.sendMessage((Component) component);
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	@Override
	public IPlatformPlayer<Player> asPlayer() {
		return this;
	}

}
