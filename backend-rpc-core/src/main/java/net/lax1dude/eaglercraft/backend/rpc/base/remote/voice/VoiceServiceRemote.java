package net.lax1dude.eaglercraft.backend.rpc.base.remote.voice;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.rpc.base.remote.EaglerXBackendRPCRemote;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.PlayerInstanceRemote;
import net.lax1dude.eaglercraft.backend.voice.api.ICEServerEntry;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.voice.api.IVoicePlayer;

public class VoiceServiceRemote<PlayerObject> implements IVoiceServiceImpl<PlayerObject> {

	private final EaglerXBackendRPCRemote<PlayerObject> server;
	private Collection<ICEServerEntry> iceServers;
	private String[] iceServersStr;
	private boolean iceOverride = false;

	public VoiceServiceRemote(EaglerXBackendRPCRemote<PlayerObject> server) {
		this.server = server;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVoiceChannel getGlobalVoiceChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVoiceChannel getDisabledVoiceChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IVoicePlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VoiceManagerRemote<PlayerObject> createVoiceManager(PlayerInstanceRemote<PlayerObject> player) {
		// TODO Auto-generated method stub
		return null;
	}

}
