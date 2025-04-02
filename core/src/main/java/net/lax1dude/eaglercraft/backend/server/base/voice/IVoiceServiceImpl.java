package net.lax1dude.eaglercraft.backend.server.base.voice;

import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceServiceX;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;

public interface IVoiceServiceImpl<PlayerObject> extends IVoiceServiceX<PlayerObject> {

	IVoiceManagerImpl<PlayerObject> createVoiceManager(EaglerPlayerInstance<PlayerObject> player);

}
