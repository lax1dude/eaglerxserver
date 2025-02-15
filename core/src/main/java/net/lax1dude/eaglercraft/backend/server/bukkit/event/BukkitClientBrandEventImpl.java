package net.lax1dude.eaglercraft.backend.server.bukkit.event;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftClientBrandEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

class BukkitClientBrandEventImpl extends EaglercraftClientBrandEvent {

	private final IEaglerXServerAPI<Player> api;
	private boolean cancelled;
	private final IEaglerPendingConnection pendingConnection;
	private BaseComponent message;

	BukkitClientBrandEventImpl(IEaglerXServerAPI<Player> api, IEaglerPendingConnection pendingConnection) {
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
	public BaseComponent getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = new TextComponent(message);
	}

	@Override
	public void setMessage(BaseComponent message) {
		this.message = message;
	}

}
