package net.lax1dude.eaglercraft.backend.rpc.base.remote.voice;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.ICEServerEntry;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.EaglerXBackendRPCRemote;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.PlayerInstanceRemote;

public class VoiceServiceRemote<PlayerObject> implements IVoiceServiceImpl<PlayerObject> {

	private final EaglerXBackendRPCRemote<PlayerObject> server;
	private final IVoiceChannel globalChannel;
	private Collection<ICEServerEntry> iceServers;
	private String[] iceServersStr;
	private boolean iceOverride = false;

	public VoiceServiceRemote(EaglerXBackendRPCRemote<PlayerObject> server) {
		this.server = server;
		this.globalChannel = new ManagedChannel<>(this);
	}

	@Override
	public VoiceManagerRemote<PlayerObject> createVoiceManager(PlayerInstanceRemote<PlayerObject> player) {
		return new VoiceManagerRemote<>(player, this);
	}

	@Override
	public EaglerXBackendRPCRemote<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public boolean isVoiceEnabled() {
		return true;
	}

	@Override
	public Collection<ICEServerEntry> getICEServers() {
		return iceServers;
	}

	@Override
	public void setICEServers(Collection<ICEServerEntry> newICEServers) {
		newICEServers = iceServers = ImmutableList.copyOf(newICEServers);
		iceServersStr = prepareICEServers(newICEServers);
	}

	static String[] prepareICEServers(Collection<ICEServerEntry> newICEServers) {
		String[] newArray = new String[newICEServers.size()];
		int i = 0;
		for(ICEServerEntry etr : newICEServers) {
			newArray[i++] = etr.toString();
		}
		if(i != newArray.length) {
			throw new IllegalStateException("fuck you");
		}
		return newArray;
	}

	String[] getICEServersStr() {
		return iceServersStr;
	}

	@Override
	public boolean getOverrideICEServers() {
		return iceOverride;
	}

	@Override
	public void setOverrideICEServers(boolean enable) {
		iceOverride = enable;
	}

	@Override
	public IVoiceChannel createVoiceChannel() {
		return new VoiceChannel<>(this);
	}

	@Override
	public IVoiceChannel getGlobalVoiceChannel() {
		return globalChannel;
	}

	@Override
	public IVoiceChannel getDisabledVoiceChannel() {
		return DisabledChannel.INSTANCE;
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel) {
		if(channel == null) {
			throw new NullPointerException("Voice channel cannot be null!");
		}
		if(channel == DisabledChannel.INSTANCE) {
			throw new UnsupportedOperationException("Cannot list players connected to the disabled channel");
		}
		if(!(channel instanceof VoiceChannel ch) || ch.owner != this) {
			throw new IllegalArgumentException("Unknown voice channel");
		}
		return ch.listConnectedPlayers();
	}

}
