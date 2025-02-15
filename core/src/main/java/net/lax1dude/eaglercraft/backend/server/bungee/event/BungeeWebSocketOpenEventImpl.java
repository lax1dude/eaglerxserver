package net.lax1dude.eaglercraft.backend.server.bungee.event;

import java.net.SocketAddress;

import net.lax1dude.eaglercraft.backend.server.adapter.event.IWebSocketOpenDelegate;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftWebSocketOpenEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeWebSocketOpenEventImpl extends EaglercraftWebSocketOpenEvent {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private boolean cancelled;
	private final IWebSocketOpenDelegate delegate;

	BungeeWebSocketOpenEventImpl(IEaglerXServerAPI<ProxiedPlayer> api, IWebSocketOpenDelegate delegate) {
		this.api = api;
		this.delegate = delegate;
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
	public String getHeader(EnumWebSocketHeader header) {
		return delegate.getHeader(header);
	}

	@Override
	public SocketAddress getSocketAddress() {
		return delegate.getSocketAddress();
	}

	@Override
	public String getRealIP() {
		return delegate.getRealIP();
	}

}
