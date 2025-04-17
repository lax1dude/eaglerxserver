package net.lax1dude.eaglercraft.backend.rpc.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;

public interface IEaglerPlayer<PlayerObject> extends IBasePlayer<PlayerObject> {

	boolean isVoiceCapable();

	@Nullable
	IVoiceManager<PlayerObject> getVoiceManager();

	@Nonnull
	@Override
	IRPCHandle<IEaglerPlayerRPC<PlayerObject>> getHandle();

}
