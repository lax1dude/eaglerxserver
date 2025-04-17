package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

	public EaglercraftLoginEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IEaglerLoginConnection loginConnection, boolean redirectSupport, @Nonnull String requestedServer,
			@Nonnull Callback<IEaglercraftLoginEvent<ProxiedPlayer, BaseComponent>> cb) {
		super(cb);
		this.api = api;
		this.loginConnection = loginConnection;
		this.redirectSupport = redirectSupport;
		this.username = loginConnection.getUsername();
		this.uuid = loginConnection.getUniqueId();
		this.requestedServer = requestedServer;
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
	public IEaglerLoginConnection getLoginConnection() {
		return loginConnection;
	}

	@Nullable
	@Override
	public BaseComponent getMessage() {
		return message;
	}

	@Override
	public void setMessage(@Nullable String message) {
		this.message = message != null ? new TextComponent(message) : null;
	}

	@Override
	public void setMessage(@Nullable BaseComponent message) {
		this.message = message;
	}

	@Override
	public boolean isLoginStateRedirectSupported() {
		return redirectSupport;
	}

	@Nullable
	@Override
	public String getRedirectAddress() {
		return redirect;
	}

	@Override
	public void setRedirectAddress(@Nullable String addr) {
		this.redirect = addr;
	}

	@Nonnull
	@Override
	public String getProfileUsername() {
		return username;
	}

	@Override
	public void setProfileUsername(@Nonnull String username) {
		this.username = username;
	}

	@Nonnull
	@Override
	public UUID getProfileUUID() {
		return uuid;
	}

	@Override
	public void setProfileUUID(@Nonnull UUID uuid) {
		if(uuid == null) {
			throw new NullPointerException("uuid");
		}
		this.uuid = uuid;
	}

	@Nonnull
	@Override
	public String getRequestedServer() {
		return requestedServer;
	}

	@Override
	public void setRequestedServer(@Nonnull String server) {
		if(server == null) {
			throw new NullPointerException("server");
		}
		this.requestedServer = server;
	}

}
