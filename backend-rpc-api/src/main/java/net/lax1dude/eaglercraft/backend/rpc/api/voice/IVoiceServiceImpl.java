package net.lax1dude.eaglercraft.backend.rpc.api.voice;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXServerRPC;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceService;

public interface IVoiceServiceImpl<PlayerObject> extends IVoiceService<PlayerObject> {

	IEaglerXServerRPC<PlayerObject> getServerAPI();

	default IVoiceManagerImpl<PlayerObject> getVoiceManager(PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getVoiceManager() : null;
	}

	default IVoiceManagerImpl<PlayerObject> getVoiceManager(IEaglerPlayer<PlayerObject> player) {
		return player.getVoiceManager();
	}

}
