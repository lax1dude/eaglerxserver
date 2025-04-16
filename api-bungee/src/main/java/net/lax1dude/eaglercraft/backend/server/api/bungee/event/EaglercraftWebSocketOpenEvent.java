package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.handler.codec.http.FullHttpRequest;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebSocketOpenEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public final class EaglercraftWebSocketOpenEvent extends Event implements IEaglercraftWebSocketOpenEvent<ProxiedPlayer>,
		IEaglercraftWebSocketOpenEvent.NettyUnsafe, Cancellable {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private boolean cancelled;
	private final IEaglerConnection connection;
	private final FullHttpRequest request;

	public EaglercraftWebSocketOpenEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IEaglerConnection connection, @Nonnull FullHttpRequest request) {
		this.api = api;
		this.connection = connection;
		this.request = request;
	}

	@Nonnull
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

	@Nonnull
	@Override
	public IEaglerConnection getConnection() {
		return connection;
	}

	@Nullable
	@Override
	public String getRawHeader(String name) {
		return request.headers().get(name);
	}

	@Nonnull
	@Override
	public List<String> getRawHeaders(String name) {
		return request.headers().getAll(name);
	}

	@Nonnull
	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Nonnull
	@Override
	public FullHttpRequest getHttpRequest() {
		return request;
	}

}
