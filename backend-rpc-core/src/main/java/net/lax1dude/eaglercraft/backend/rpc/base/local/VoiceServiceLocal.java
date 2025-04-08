package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.ICEServerEntry;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;

public class VoiceServiceLocal<PlayerObject> implements IVoiceService<PlayerObject> {

	private final EaglerXBackendRPCLocal<PlayerObject> server;
	private final net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceService<PlayerObject> delegate;
	private final net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel disabledChannelDelegate;
	private final IVoiceChannel disabledChannel;
	private final net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel globalChannelDelegate;
	private final IVoiceChannel globalChannel;

	VoiceServiceLocal(EaglerXBackendRPCLocal<PlayerObject> server,
			net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceService<PlayerObject> delegate) {
		this.server = server;
		this.delegate = delegate;
		this.disabledChannelDelegate = delegate.getDisabledVoiceChannel();
		this.disabledChannel = VoiceChannelHelper.wrap(disabledChannelDelegate);
		this.globalChannelDelegate = delegate.getGlobalVoiceChannel();
		this.globalChannel = VoiceChannelHelper.wrap(globalChannelDelegate);
	}

	IVoiceChannel wrapConst(net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel channel) {
		if(channel == disabledChannelDelegate) {
			return disabledChannel;
		}else if(channel == globalChannelDelegate) {
			return globalChannel;
		}else {
			return VoiceChannelHelper.wrap(channel);
		}
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
		return delegate.getICEServers().stream().map(VoiceChannelHelper::wrap).collect(ImmutableList.toImmutableList());
	}

	@Override
	public void setICEServers(Collection<ICEServerEntry> servers) {
		delegate.setICEServers(servers.stream().map(VoiceChannelHelper::unwrap).collect(ImmutableList.toImmutableList()));
	}

	@Override
	public IVoiceChannel createVoiceChannel() {
		return VoiceChannelHelper.wrap(delegate.createVoiceChannel());
	}

	@Override
	public IVoiceChannel getGlobalVoiceChannel() {
		return globalChannel;
	}

	@Override
	public IVoiceChannel getDisabledVoiceChannel() {
		return disabledChannel;
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel) {
		Collection<net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer<PlayerObject>> playersDelegate = delegate
				.getConnectedPlayers(VoiceChannelHelper.unwrap(channel));
		if(playersDelegate.isEmpty()) {
			return Collections.emptyList();
		}
		ImmutableList.Builder<IEaglerPlayer<PlayerObject>> ret = ImmutableList.builderWithExpectedSize(playersDelegate.size());
		for(net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer<PlayerObject> player : playersDelegate) {
			IEaglerPlayer<PlayerObject> pp = server.getEaglerPlayer(player.getPlayerObject());
			if(pp != null) {
				ret.add(pp);
			}
		}
		return ret.build();
	}

	@Override
	public boolean getOverrideICEServers() {
		return true;
	}

	@Override
	public void setOverrideICEServers(boolean enable) {
	}

}
