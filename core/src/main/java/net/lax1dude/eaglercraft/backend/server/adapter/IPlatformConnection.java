package net.lax1dude.eaglercraft.backend.server.adapter;

import java.net.SocketAddress;
import java.util.UUID;

import io.netty.channel.Channel;

public interface IPlatformConnection {

	Channel getChannel();

	<T> T getAttachment();

	String getUsername();

	UUID getUniqueId();

	SocketAddress getSocketAddress();

	int getMinecraftProtocol();

	boolean isOnlineMode();

	boolean isConnected();

	void disconnect();

	<ComponentObject> void disconnect(ComponentObject kickMessage);

}
