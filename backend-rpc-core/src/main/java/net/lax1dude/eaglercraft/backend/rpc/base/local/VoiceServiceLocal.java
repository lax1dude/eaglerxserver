package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.util.Collection;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceServiceX;
import net.lax1dude.eaglercraft.backend.voice.api.ICEServerEntry;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.voice.api.IVoicePlayer;

public class VoiceServiceLocal<PlayerObject> implements IVoiceServiceX<PlayerObject> {

	private final EaglerXBackendRPCLocal<PlayerObject> server;
	private final net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceServiceX<PlayerObject> delegate;

	VoiceServiceLocal(EaglerXBackendRPCLocal<PlayerObject> server,
			net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceServiceX<PlayerObject> delegate) {
		this.server = server;
		this.delegate = delegate;
	}

	@Override
	public IEaglerXBackendRPC<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public boolean isVoiceEnabled() {
		return delegate.isVoiceEnabled();
	}

	@Override
	public Collection<ICEServerEntry> getICEServers() {
		return delegate.getICEServers();
	}

	@Override
	public void setICEServers(Collection<ICEServerEntry> servers) {
		delegate.setICEServers(servers);
	}

	@Override
	public IVoiceChannel createVoiceChannel() {
		return delegate.createVoiceChannel();
	}

	@Override
	public IVoiceChannel getGlobalVoiceChannel() {
		return delegate.getGlobalVoiceChannel();
	}

	@Override
	public IVoiceChannel getDisabledVoiceChannel() {
		return delegate.getDisabledVoiceChannel();
	}

	@Override
	public Collection<IVoicePlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel) {
		return delegate.getConnectedPlayers(channel);
	}

	@Override
	public boolean getOverrideICEServers() {
		return true;
	}

	@Override
	public void setOverrideICEServers(boolean enable) {
	}

}
