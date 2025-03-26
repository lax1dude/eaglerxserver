package net.lax1dude.eaglercraft.backend.server.api.voice;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceService;

public interface IVoiceServiceX<PlayerObject> extends IVoiceService<PlayerObject> {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	default IVoiceManagerX<PlayerObject> getVoiceManager(PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getVoiceManager() : null;
	}

	default IVoiceManagerX<PlayerObject> getVoiceManager(IEaglerPlayer<PlayerObject> player) {
		return player.getVoiceManager();
	}

	boolean isBackendRelayMode();

}
