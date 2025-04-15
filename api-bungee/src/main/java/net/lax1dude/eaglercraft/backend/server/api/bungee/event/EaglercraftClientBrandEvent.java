package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.plugin.Cancellable;

public final class EaglercraftClientBrandEvent
		extends AsyncEvent<IEaglercraftClientBrandEvent<ProxiedPlayer, BaseComponent>>
		implements IEaglercraftClientBrandEvent<ProxiedPlayer, BaseComponent>, Cancellable {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private boolean cancelled;
	private final IEaglerPendingConnection pendingConnection;
	private BaseComponent message;

	public EaglercraftClientBrandEvent(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerPendingConnection pendingConnection,
			Callback<IEaglercraftClientBrandEvent<ProxiedPlayer, BaseComponent>> cb) {
		super(cb);
		this.api = api;
		this.pendingConnection = pendingConnection;
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
	public IEaglerPendingConnection getPendingConnection() {
		return pendingConnection;
	}

	@Override
	public BaseComponent getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message != null ? new TextComponent(message) : null;
	}

	@Override
	public void setMessage(BaseComponent message) {
		this.message = message;
	}

}
