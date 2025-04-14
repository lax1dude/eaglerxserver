package net.lax1dude.eaglercraft.backend.rpc.adapter.event;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.event.IEaglercraftVoiceCapableEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;

public interface IEventDispatchAdapter<PlayerObject> {

	void setAPI(IEaglerXBackendRPC<PlayerObject> api);

	void dispatchPlayerReadyEvent(IEaglerPlayer<PlayerObject> player);

	IEaglercraftVoiceCapableEvent<PlayerObject> dispatchVoiceCapableEvent(IEaglerPlayer<PlayerObject> player, IVoiceChannel channel);

	void dispatchVoiceChangeEvent(IEaglerPlayer<PlayerObject> player, EnumVoiceState stateOld, EnumVoiceState stateNew);

}
