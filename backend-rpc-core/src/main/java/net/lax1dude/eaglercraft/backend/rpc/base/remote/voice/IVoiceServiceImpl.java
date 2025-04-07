package net.lax1dude.eaglercraft.backend.rpc.base.remote.voice;

import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceServiceX;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.PlayerInstanceRemote;

public interface IVoiceServiceImpl<PlayerObject> extends IVoiceServiceX<PlayerObject> {

	VoiceManagerRemote<PlayerObject> createVoiceManager(PlayerInstanceRemote<PlayerObject> player);

}
