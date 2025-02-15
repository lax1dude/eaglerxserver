package net.lax1dude.eaglercraft.backend.server.velocity.event;

import java.net.SocketAddress;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.adapter.event.IWebSocketOpenDelegate;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftWebSocketOpenEvent;

class VelocityWebSocketOpenEventImpl extends EaglercraftWebSocketOpenEvent {

	private final IEaglerXServerAPI<Player> api;
	private boolean cancelled;
	private final IWebSocketOpenDelegate delegate;

	VelocityWebSocketOpenEventImpl(IEaglerXServerAPI<Player> api, IWebSocketOpenDelegate delegate) {
		this.api = api;
		this.delegate = delegate;
	}

	@Override
	public IEaglerXServerAPI<Player> getServerAPI() {
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
