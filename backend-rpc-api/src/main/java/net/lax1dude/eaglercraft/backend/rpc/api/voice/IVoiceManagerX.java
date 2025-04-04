package net.lax1dude.eaglercraft.backend.rpc.api.voice;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceManager;

public interface IVoiceManagerX<PlayerObject> extends IVoiceManager<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IVoiceServiceX<PlayerObject> getVoiceService();

}
