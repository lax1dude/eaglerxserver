package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.util.Collection;
import java.util.Collections;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.ICEServerEntry;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;

public class VoiceServiceDisabled<PlayerObject> implements IVoiceService<PlayerObject> {

	private final EaglerXBackendRPCLocal<PlayerObject> server;

	public VoiceServiceDisabled(EaglerXBackendRPCLocal<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public IEaglerXBackendRPC<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public boolean isVoiceEnabled() {
		return false;
	}

	@Override
	public Collection<ICEServerEntry> getICEServers() {
		return Collections.emptyList();
	}

	@Override
	public void setICEServers(Collection<ICEServerEntry> servers) {
	}

	@Override
	public boolean getOverrideICEServers() {
		return false;
	}

	@Override
	public void setOverrideICEServers(boolean enable) {
	}

	@Override
	public IVoiceChannel createVoiceChannel() {
		throw disabledError();
	}

	@Override
	public IVoiceChannel getGlobalVoiceChannel() {
		throw disabledError();
	}

	@Override
	public IVoiceChannel getDisabledVoiceChannel() {
		throw disabledError();
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel) {
		throw disabledError();
	}

	private static RuntimeException disabledError() {
		return new IllegalStateException("RPC voice service is disabled!");
	}

}
