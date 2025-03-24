package net.lax1dude.eaglercraft.backend.server.api.voice;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceManager;

public interface IVoiceManagerImpl<PlayerObject> extends IVoiceManager<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IVoiceServiceImpl<PlayerObject> getVoiceService();

	boolean isBackendRelayMode();

}
