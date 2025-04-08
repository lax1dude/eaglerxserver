package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceService;

public interface IEaglercraftVoiceChangeEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	default IVoiceManager<PlayerObject> getVoiceManager() {
		return getPlayer().getVoiceManager();
	}

	default IVoiceService<PlayerObject> getVoiceService() {
		return getServerAPI().getVoiceService();
	}

	EnumVoiceState getVoiceStateOld();

	EnumVoiceState getVoiceStateNew();

}
