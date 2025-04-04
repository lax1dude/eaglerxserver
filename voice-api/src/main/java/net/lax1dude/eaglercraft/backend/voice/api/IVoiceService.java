package net.lax1dude.eaglercraft.backend.voice.api;

import java.util.Collection;

public interface IVoiceService<PlayerObject> {

	IEaglerVoiceAPI<PlayerObject> getServerAPI();

	default IVoiceManager<PlayerObject> getVoiceManager(PlayerObject player) {
		IVoicePlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getVoiceManager() : null;
	}

	default IVoiceManager<PlayerObject> getVoiceManager(IVoicePlayer<PlayerObject> player) {
		return player.getVoiceManager();
	}

	boolean isVoiceEnabled();

	Collection<ICEServerEntry> getICEServers();

	void setICEServers(Collection<ICEServerEntry> servers);

	IVoiceChannel createVoiceChannel();

	IVoiceChannel getGlobalVoiceChannel();

	IVoiceChannel getDisabledVoiceChannel();

	Collection<IVoicePlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel);

}
