package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRevokeSessionQueryEvent;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.AsyncEvent;

public final class EaglercraftRevokeSessionQueryEvent
		extends AsyncEvent<IEaglercraftRevokeSessionQueryEvent<ProxiedPlayer>>
		implements IEaglercraftRevokeSessionQueryEvent<ProxiedPlayer> {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IQueryConnection query;
	private final byte[] cookieData;
	private EnumSessionRevokeStatus result;
	private boolean shouldDelete;

	public EaglercraftRevokeSessionQueryEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IQueryConnection query, @Nonnull byte[] cookieData,
			@Nonnull Callback<IEaglercraftRevokeSessionQueryEvent<ProxiedPlayer>> cb) {
		super(cb);
		this.api = api;
		this.query = query;
		this.cookieData = cookieData;
		this.result = EnumSessionRevokeStatus.FAILED_NOT_SUPPORTED;
		this.shouldDelete = false;
	}

	@Nonnull
	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Nonnull
	@Override
	public IQueryConnection getSocket() {
		return query;
	}

	@Nonnull
	@Override
	public byte[] getCookieData() {
		return cookieData;
	}

	@Nonnull
	@Override
	public EnumSessionRevokeStatus getResultStatus() {
		return result;
	}

	@Override
	public void setResultStatus(@Nonnull EnumSessionRevokeStatus result) {
		if(result == null) {
			throw new NullPointerException("result");
		}
		this.result = result;
	}

	@Override
	public boolean getShouldDeleteCookie() {
		return shouldDelete;
	}

	@Override
	public void setShouldDeleteCookie(boolean flag) {
		shouldDelete = flag;
	}

}
