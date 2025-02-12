package net.lax1dude.eaglercraft.eaglerxserver.api.voice;

import net.lax1dude.eaglercraft.eaglerxserver.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.eaglerxserver.api.players.IEaglerPlayer;

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
