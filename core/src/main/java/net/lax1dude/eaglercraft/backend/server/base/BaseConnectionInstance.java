package net.lax1dude.eaglercraft.backend.server.base;

import java.net.SocketAddress;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.api.IBasePendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.INettyChannel;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;

public class BaseConnectionInstance implements IBasePendingConnection, INettyChannel.NettyUnsafe {

	protected final IPlatformConnection connection;
	protected final EaglerAttributeManager.EaglerAttributeHolder attributeHolder;

	public BaseConnectionInstance(IPlatformConnection connection,
			EaglerAttributeManager.EaglerAttributeHolder attributeHolder) {
		this.connection = connection;
		this.attributeHolder = attributeHolder;
	}

	@Override
	public <T> T get(IAttributeKey<T> key) {
		return attributeHolder.get(key);
	}

	@Override
	public <T> void set(IAttributeKey<T> key, T value) {
		attributeHolder.set(key, value);
	}

	@Override
	public SocketAddress getSocketAddress() {
		return connection.getSocketAddress();
	}

	@Override
	public String getRealAddress() {
		return connection.getSocketAddress().toString();
	}

	@Override
	public int getMinecraftProtocol() {
		return connection.getMinecraftProtocol();
	}

	@Override
	public boolean isEaglerPlayer() {
		return false;
	}

	@Override
	public IEaglerPendingConnection asEaglerPlayer() {
		return null;
	}

	@Override
	public boolean isOnlineMode() {
		return connection.isOnlineMode();
	}

	@Override
	public void disconnect() {
		connection.disconnect();
	}

	@Override
	public NettyUnsafe getNettyUnsafe() {
		return this;
	}

	@Override
	public Channel getChannel() {
		return connection.getChannel();
	}

}
