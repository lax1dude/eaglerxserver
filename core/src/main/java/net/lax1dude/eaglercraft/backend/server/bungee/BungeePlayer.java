package net.lax1dude.eaglercraft.backend.server.bungee;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

class BungeePlayer implements IPlatformPlayer<ProxiedPlayer> {

	private final ProxiedPlayer player;
	private final BungeeConnection connection;
	Object attachment;
	IPlatformServer<ProxiedPlayer> server;

	BungeePlayer(ProxiedPlayer player, BungeeConnection connection) {
		this.player = player;
		this.connection = connection;
		this.connection.playerInstance = player;
	}

	@Override
	public IPlatformConnection getConnection() {
		return connection;
	}

	@Override
	public ProxiedPlayer getPlayerObject() {
		return player;
	}

	@Override
	public IPlatformServer<ProxiedPlayer> getServer() {
		return server;
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
		return player.isConnected();
	}

	@Override
	public boolean isOnlineMode() {
		return player.getPendingConnection().isOnlineMode();
	}

	@Override
	public String getMinecraftBrand() {
		byte[] ret = BungeeUnsafe.getBrandMessage(player.getPendingConnection());
		if(ret != null && ret.length > 0) {
			int len = (int)ret[0] & 0xFF;
			if(len < 128 && len == ret.length - 1) {
				return new String(ret, 1, len, StandardCharsets.UTF_8);
			}else {
				// Brand over 127 chars is probably garbage anyway...
				return null;
			}
		}else {
			return null;
		}
	}

	@Override
	public void sendDataClient(String channel, byte[] message) {
		player.sendData(channel, message);
	}

	@Override
	public void sendDataBackend(String channel, byte[] message) {
		Server server = player.getServer();
		if(server != null) {
			server.sendData(channel, message);
		}
	}

	@Override
	public boolean isSetViewDistanceSupportedPaper() {
		return false;
	}

	@Override
	public void setViewDistancePaper(int distance) {
	}

	@Override
	public String getTexturesProperty() {
		return BungeeUnsafe.getTexturesProperty(player.getPendingConnection());
	}

	@Override
	public void disconnect() {
		player.disconnect(BungeeConnection.DEFAULT_KICK_MESSAGE);
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		player.disconnect((BaseComponent)kickMessage);
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
		player.sendMessage((BaseComponent) component);
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	@Override
	public IPlatformPlayer<ProxiedPlayer> asPlayer() {
		return this;
	}

}
