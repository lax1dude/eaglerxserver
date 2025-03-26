package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManagerX;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceServiceX;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;

public interface IEaglercraftVoiceChangeEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	default IVoiceManagerX<PlayerObject> getVoiceManager() {
		return getPlayer().getVoiceManager();
	}

	default IVoiceServiceX<PlayerObject> getVoiceService() {
		return getServerAPI().getVoiceService();
	}

	EnumVoiceState getVoiceStateOld();

	EnumVoiceState getVoiceStateNew();

}
