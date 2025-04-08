package net.lax1dude.eaglercraft.backend.server.api.voice;

import java.util.Collection;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IVoiceService<PlayerObject> {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	default IVoiceManager<PlayerObject> getVoiceManager(PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getVoiceManager() : null;
	}

	default IVoiceManager<PlayerObject> getVoiceManager(IEaglerPlayer<PlayerObject> player) {
		return player.getVoiceManager();
	}

	boolean isVoiceEnabled();

	boolean isVoiceEnabledAllServers();

	boolean isVoiceEnabledOnServer(String serverName);

	boolean isSeparateServerChannels();

	IVoiceChannel getServerVoiceChannel(String serverName);

	boolean isBackendRelayMode();

	Collection<ICEServerEntry> getICEServers();

	void setICEServers(Collection<ICEServerEntry> servers);

	IVoiceChannel createVoiceChannel();

	IVoiceChannel getGlobalVoiceChannel();

	IVoiceChannel getDisabledVoiceChannel();

	Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(IVoiceChannel channel);

}
