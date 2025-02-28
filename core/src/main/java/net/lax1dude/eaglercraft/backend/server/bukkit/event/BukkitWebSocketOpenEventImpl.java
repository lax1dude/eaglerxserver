package net.lax1dude.eaglercraft.backend.server.bukkit.event;

import org.bukkit.entity.Player;

import java.net.SocketAddress;

import net.lax1dude.eaglercraft.backend.server.adapter.event.IWebSocketOpenDelegate;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftWebSocketOpenEvent;

class BukkitWebSocketOpenEventImpl extends EaglercraftWebSocketOpenEvent {

	private final IEaglerXServerAPI<Player> api;
	private boolean cancelled;
	private final IWebSocketOpenDelegate delegate;

	BukkitWebSocketOpenEventImpl(IEaglerXServerAPI<Player> api, IWebSocketOpenDelegate delegate) {
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
		return delegate.getWebSocketHeader(header);
	}

	@Override
	public SocketAddress getSocketAddress() {
		return delegate.getSocketAddress();
	}

	@Override
	public String getRealIP() {
		return delegate.getRealAddress();
	}

}
