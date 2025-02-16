package net.lax1dude.eaglercraft.backend.server.bungee;

import java.net.SocketAddress;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.PendingConnection;

class BungeeConnection implements IPlatformConnection {

	private final PlatformPluginBungee platformPlugin;
	private final boolean eagler;
	private final PendingConnection pendingConnection;
	Object attachment;

	BungeeConnection(PlatformPluginBungee platformPlugin, boolean eagler, PendingConnection pendingConnection) {
		this.platformPlugin = platformPlugin;
		this.eagler = eagler;
		this.pendingConnection = pendingConnection;
	}

	@Override
	public <T> T getAttachment() {
		return (T) attachment;
	}

	@Override
	public boolean isEaglerConnection() {
		return eagler;
	}

	@Override
	public boolean isOnlineMode() {
		return pendingConnection.isOnlineMode();
	}

	@Override
	public String getUsername() {
		return pendingConnection.getName();
	}

	@Override
	public UUID getUniqueId() {
		return pendingConnection.getUniqueId();
	}

	@Override
	public SocketAddress getSocketAddress() {
		return pendingConnection.getSocketAddress();
	}

	@Override
	public int getMinecraftProtocol() {
		return pendingConnection.getVersion();
	}

	@Override
	public void disconnect() {
		pendingConnection.disconnect();
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		pendingConnection.disconnect((BaseComponent)kickMessage);
	}

}
