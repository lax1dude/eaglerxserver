package net.lax1dude.eaglercraft.backend.rpc.api.voice;

import java.util.Collection;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceService;

public interface IVoiceServiceX<PlayerObject> extends IVoiceService<PlayerObject> {

	IEaglerXBackendRPC<PlayerObject> getServerAPI();

	boolean getOverrideICEServers();

	void setOverrideICEServers(boolean enable);

	default IVoiceManagerX<PlayerObject> getVoiceManager(PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getVoiceManager() : null;
	}

	default IVoiceManagerX<PlayerObject> getVoiceManager(IEaglerPlayer<PlayerObject> player) {
		return player.getVoiceManager();
	}

	@SuppressWarnings("unchecked")
	default Collection<IEaglerPlayer<PlayerObject>> getConnectedEaglerPlayers(IVoiceChannel channel) {
		// Nothing to see here...
		return (Collection<IEaglerPlayer<PlayerObject>>) (Object) getConnectedPlayers(channel);
	}

}
