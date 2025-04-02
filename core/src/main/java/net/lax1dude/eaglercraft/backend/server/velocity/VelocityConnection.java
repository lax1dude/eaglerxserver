package net.lax1dude.eaglercraft.backend.server.velocity;

import java.net.SocketAddress;
import java.util.UUID;

import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;

import io.netty.channel.Channel;
import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;

class VelocityConnection implements IPlatformConnection {

	static final Component DEFAULT_KICK_MESSAGE = Component.translatable("disconnect.closed");

	private final PlatformPluginVelocity plugin;
	private final InboundConnection connection;
	private final String username;
	UUID uuid;
	String texturesPropertyValue;
	String texturesPropertySignature;
	boolean eaglerPlayerProperty;
	boolean compressionDisable;
	volatile Player playerInstance;
	Object attachment;

	VelocityConnection(PlatformPluginVelocity plugin, InboundConnection connection,
			String username, UUID uuid) {
		this.plugin = plugin;
		this.connection = connection;
		this.username = username;
		this.uuid = uuid;
	}

	@Override
	public Channel getChannel() {
		return VelocityUnsafe.getInboundChannel(connection);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAttachment() {
		return (T) attachment;
	}

	@Override
	public String getUsername() {
		Player player = playerInstance;
		if(player != null) {
			return player.getUsername();
		}else {
			return username;
		}
	}

	@Override
	public UUID getUniqueId() {
		Player player = playerInstance;
		if(player != null) {
			return player.getUniqueId();
		}else {
			return uuid;
		}
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
	public boolean isOnlineMode() {
		Player player = playerInstance;
		if(player != null) {
			return player.isOnlineMode();
		}else {
			//TODO: online mode?
			return false;
		}
	}

	@Override
	public boolean isConnected() {
		Player player = playerInstance;
		if(player != null) {
			return player.isActive();
		}else {
			return connection.isActive();
		}
	}

	@Override
	public void disconnect() {
		Player player = playerInstance;
		if(player != null) {
			player.disconnect(DEFAULT_KICK_MESSAGE);
		}else {
			VelocityUnsafe.disconnectInbound(connection);
		}
	}

	@Override
	public <ComponentObject> void disconnect(ComponentObject kickMessage) {
		Player player = playerInstance;
		if(player != null) {
			player.disconnect((Component)kickMessage);
		}else {
			VelocityUnsafe.disconnectInbound(connection, (Component)kickMessage);
		}
	}

}
