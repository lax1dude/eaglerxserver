package net.lax1dude.eaglercraft.backend.server.api.voice;

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

	boolean isBackendRelayMode();

	boolean isManagedPerServer();

	IVoiceChannel createVoiceChannel();

	IVoiceChannel getGlobalVoiceChannel();

	IVoiceChannel getServerVoiceChannel(String serverName);

}
