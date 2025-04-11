package net.lax1dude.eaglercraft.backend.rpc.api;

import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;

public interface IEaglerPlayer<PlayerObject> extends IBasePlayer<PlayerObject> {

	boolean isVoiceCapable();

	IVoiceManager<PlayerObject> getVoiceManager();

	IRPCHandle<IEaglerPlayerRPC<PlayerObject>> getHandle();

}
