package net.lax1dude.eaglercraft.backend.server.api.voice;

import java.util.Collection;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceChannel;
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

	boolean isVoiceEnabledAllServers();

	boolean isVoiceEnabledOnServer(String serverName);

	boolean isSeparateServerChannels();

	IVoiceChannel getServerVoiceChannel(String serverName);

	boolean isBackendRelayMode();

	@SuppressWarnings("unchecked")
	default Collection<IEaglerPlayer<PlayerObject>> getConnectedEaglerPlayers(IVoiceChannel channel) {
		// Nothing to see here...
		return (Collection<IEaglerPlayer<PlayerObject>>) (Object) getConnectedPlayers(channel);
	}

}
