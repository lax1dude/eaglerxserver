package net.lax1dude.eaglercraft.backend.server.api.voice;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceManager;

public interface IVoiceManagerX<PlayerObject> extends IVoiceManager<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IVoiceServiceX<PlayerObject> getVoiceService();

	boolean isServerManaged();

	void setServerManaged(boolean managed);

	boolean isBackendRelayMode();

}
