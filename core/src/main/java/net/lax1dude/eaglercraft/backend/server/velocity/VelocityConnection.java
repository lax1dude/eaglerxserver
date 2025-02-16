package net.lax1dude.eaglercraft.backend.server.velocity;

import java.net.SocketAddress;
import java.util.UUID;

import com.velocitypowered.api.proxy.InboundConnection;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;

class VelocityConnection implements IPlatformConnection {

	private final PlatformPluginVelocity plugin;
	private final boolean eagler;
	private final InboundConnection connection;
	private final String username;
	private final UUID uuid;
	private final boolean online;
	Object attachment;

	VelocityConnection(PlatformPluginVelocity plugin, boolean eagler, InboundConnection connection,
			String username, UUID uuid, boolean online) {
		this.plugin = plugin;
		this.eagler = eagler;
		this.connection = connection;
		this.username = username;
		this.uuid = uuid;
		this.online = online;
	}

	@Override
	public <T> T getAttachment() {
		return (T) attachment;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public SocketAddress getSocketAddress() {
		return connection.getRemoteAddress();
	}

	@Override
	public int getMinecraftProtocol() {
		return connection.getProtocolVersion().getProtocol();
	}

	@Override
	public boolean isEaglerConnection() {
		return eagler;
	}

	@Override
	public boolean isOnlineMode() {
		return online;
	}

	@Override
	public void disconnect() {
		VelocityUnsafe.disconnectInbound(connection);
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		VelocityUnsafe.disconnectInbound(connection, (Component)kickMessage);
	}

}
