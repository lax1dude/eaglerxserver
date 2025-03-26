package net.lax1dude.eaglercraft.backend.rpc.api;

import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManagerX;
import net.lax1dude.eaglercraft.backend.voice.api.IVoicePlayer;

public interface IEaglerPlayer<PlayerObject> extends IBasePlayer<PlayerObject>, IVoicePlayer<PlayerObject> {

	boolean isVoiceCapable();

	boolean hasVoiceManager();

	IVoiceManagerX<PlayerObject> getVoiceManager();

	IRPCHandle<IEaglerPlayerRPC<PlayerObject>> getHandleEagler();

}
