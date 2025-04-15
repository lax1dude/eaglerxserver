package net.lax1dude.eaglercraft.backend.server.velocity.event;

import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftClientBrandEvent;

class VelocityClientBrandEventImpl extends EaglercraftClientBrandEvent {

	private final IEaglerXServerAPI<Player> api;
	private boolean cancelled;
	private final IEaglerPendingConnection pendingConnection;
	private Component message;

	VelocityClientBrandEventImpl(IEaglerXServerAPI<Player> api, IEaglerPendingConnection pendingConnection) {
		this.api = api;
		this.pendingConnection = pendingConnection;
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
	public IEaglerPendingConnection getPendingConnection() {
		return pendingConnection;
	}

	@Override
	public Component getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message != null ? Component.text(message) : null;
	}

	@Override
	public void setMessage(Component message) {
		this.message = message;
	}

}
