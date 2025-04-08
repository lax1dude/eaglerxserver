package net.lax1dude.eaglercraft.backend.rpc.api.voice;

import java.util.Collection;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;

public interface IVoiceService<PlayerObject> {

	IEaglerXBackendRPC<PlayerObject> getServerAPI();

	default IVoiceManager<PlayerObject> getVoiceManager(PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getVoiceManager() : null;
	}

	default IVoiceManager<PlayerObject> getVoiceManager(IEaglerPlayer<PlayerObject> player) {
		return player.getVoiceManager();
	}

	boolean isVoiceEnabled();

	Collection<ICEServerEntry> getICEServers();

	void setICEServers(Collection<ICEServerEntry> servers);

	boolean getOverrideICEServers();

	void setOverrideICEServers(boolean enable);

	IVoiceChannel createVoiceChannel();

	IVoiceChannel getGlobalVoiceChannel();

	IVoiceChannel getDisabledVoiceChannel();

	Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel);

}
