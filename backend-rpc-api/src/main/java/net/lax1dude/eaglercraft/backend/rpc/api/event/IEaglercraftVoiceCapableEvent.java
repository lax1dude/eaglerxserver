package net.lax1dude.eaglercraft.backend.rpc.api.event;

import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManagerX;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceServiceX;

public interface IEaglercraftVoiceCapableEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	default IVoiceManagerX<PlayerObject> getVoiceManager() {
		return getPlayer().getVoiceManager();
	}

	default IVoiceServiceX<PlayerObject> getVoiceService() {
		return getServerAPI().getVoiceService();
	}

}
