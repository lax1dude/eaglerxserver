package net.lax1dude.eaglercraft.backend.server.base;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerLoginConnection;

public class EaglerLoginStateAdapter extends EaglerPendingStateAdapter implements IEaglerLoginConnection {

	EaglerLoginStateAdapter(NettyPipelineData pipelineData) {
		super(pipelineData);
	}

	@Override
	public IEaglerLoginConnection asEaglerPlayer() {
		return this;
	}

	@Override
	public UUID getUniqueId() {
		return pipelineData.uuid;
	}

	@Override
	public String getUsername() {
		return pipelineData.username;
	}

	@Override
	public boolean isOnlineMode() {
		return false;
	}

	@Override
	public boolean isCookieSupported() {
		return pipelineData.gameProtocol.ver >= 4;
	}

	@Override
	public boolean isCookieEnabled() {
		return pipelineData.cookieEnabled;
	}

	@Override
	public byte[] getCookieData() {
		return pipelineData.cookieData;
	}

}
