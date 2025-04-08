package net.lax1dude.eaglercraft.backend.rpc.api.event;

import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;

public interface IEaglercraftVoiceCapableEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	default IVoiceManager<PlayerObject> getVoiceManager() {
		return getPlayer().getVoiceManager();
	}

	default IVoiceService<PlayerObject> getVoiceService() {
		return getServerAPI().getVoiceService();
	}

}
