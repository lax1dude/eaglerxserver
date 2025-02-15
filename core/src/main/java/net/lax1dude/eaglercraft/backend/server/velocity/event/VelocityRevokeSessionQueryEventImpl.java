package net.lax1dude.eaglercraft.backend.server.velocity.event;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryConnection;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftRevokeSessionQueryEvent;

class VelocityRevokeSessionQueryEventImpl extends EaglercraftRevokeSessionQueryEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IQueryConnection query;
	private final byte[] cookieData;
	private EnumSessionRevokeStatus result;
	private boolean shouldDelete;

	VelocityRevokeSessionQueryEventImpl(IEaglerXServerAPI<Player> api, IQueryConnection query,
			byte[] cookieData) {
		this.api = api;
		this.query = query;
		this.cookieData = cookieData;
		this.result = EnumSessionRevokeStatus.FAILED_NOT_SUPPORTED;
		this.shouldDelete = false;
	}

	@Override
	public IEaglerXServerAPI<Player> getServerAPI() {
		return api;
	}

	@Override
	public IQueryConnection getSocket() {
		return query;
	}

	@Override
	public byte[] getCookieData() {
		return cookieData;
	}

	@Override
	public EnumSessionRevokeStatus getResultStatus() {
		return result;
	}

	@Override
	public void setResultStatus(EnumSessionRevokeStatus result) {
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
