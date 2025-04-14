package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftLoginEvent;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.plugin.Cancellable;

public final class EaglercraftLoginEvent
		extends AsyncEvent<IEaglercraftLoginEvent<ProxiedPlayer, BaseComponent>>
		implements IEaglercraftLoginEvent<ProxiedPlayer, BaseComponent>, Cancellable {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private boolean cancelled;
	private final IEaglerLoginConnection loginConnection;
	private final boolean redirectSupport;
	private BaseComponent message;
	private String redirect;
	private String username;
	private UUID uuid;
	private String requestedServer;

	public EaglercraftLoginEvent(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerLoginConnection loginConnection,
			boolean redirectSupport, String requestedServer,
			Callback<IEaglercraftLoginEvent<ProxiedPlayer, BaseComponent>> cb) {
		super(cb);
		this.api = api;
		this.loginConnection = loginConnection;
		this.redirectSupport = redirectSupport;
		this.username = loginConnection.getUsername();
		this.uuid = loginConnection.getUniqueId();
		this.requestedServer = requestedServer;
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
	public IEaglerLoginConnection getLoginConnection() {
		return loginConnection;
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

	@Override
	public boolean isLoginStateRedirectSupported() {
		return redirectSupport;
	}

	@Override
	public String getRedirectAddress() {
		return redirect;
	}

	@Override
	public void setRedirectAddress(String addr) {
		this.redirect = addr;
	}

	@Override
	public String getProfileUsername() {
		return username;
	}

	@Override
	public void setProfileUsername(String username) {
		this.username = username;
	}

	@Override
	public UUID getProfileUUID() {
		return uuid;
	}

	@Override
	public void setProfileUUID(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getRequestedServer() {
		return requestedServer;
	}

	@Override
	public void setRequestedServer(String server) {
		this.requestedServer = server;
	}

}
