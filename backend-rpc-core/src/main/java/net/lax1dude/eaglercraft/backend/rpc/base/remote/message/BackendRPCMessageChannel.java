package net.lax1dude.eaglercraft.backend.rpc.base.remote.message;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCMessageChannel;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCMessageHandler;

public class BackendRPCMessageChannel<PlayerObject> implements IBackendRPCMessageChannel<PlayerObject> {

	private final String legacyName;
	private final String modernName;
	private final IBackendRPCMessageHandler<PlayerObject> handler;

	public BackendRPCMessageChannel(String legacyName, String modernName,
			IBackendRPCMessageHandler<PlayerObject> handler) {
		this.legacyName = legacyName;
		this.modernName = modernName;
		this.handler = handler;
	}

	@Override
	public String getLegacyName() {
		return legacyName;
	}

	@Override
	public String getModernName() {
		return modernName;
	}

	@Override
	public IBackendRPCMessageHandler<PlayerObject> getHandler() {
		return handler;
	}

}
