package net.lax1dude.eaglercraft.backend.server.bungee.event;

import java.util.List;

import io.netty.handler.codec.http.FullHttpRequest;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftWebSocketOpenEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebSocketOpenEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeWebSocketOpenEventImpl extends EaglercraftWebSocketOpenEvent
		implements IEaglercraftWebSocketOpenEvent.NettyUnsafe {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private boolean cancelled;
	private final IEaglerConnection connection;
	private final FullHttpRequest request;

	BungeeWebSocketOpenEventImpl(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerConnection connection,
			FullHttpRequest request) {
		this.api = api;
		this.connection = connection;
		this.request = request;
	}

	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public IEaglerConnection getConnection() {
		return connection;
	}

	@Override
	public String getRawHeader(String name) {
		return request.headers().get(name);
	}

	@Override
	public List<String> getRawHeaders(String name) {
		return request.headers().getAll(name);
	}

	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Override
	public FullHttpRequest getHttpRequest() {
		return request;
	}

}
