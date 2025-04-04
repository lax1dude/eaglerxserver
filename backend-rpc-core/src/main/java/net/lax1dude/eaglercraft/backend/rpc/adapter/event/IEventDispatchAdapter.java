package net.lax1dude.eaglercraft.backend.rpc.adapter.event;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;

public interface IEventDispatchAdapter<PlayerObject> {

	void setAPI(IEaglerXBackendRPC<PlayerObject> api);

	void dispatchPlayerReadyEvent(IEaglerPlayer<PlayerObject> player);

	void dispatchVoiceCapableEvent(IEaglerPlayer<PlayerObject> player);

	void dispatchVoiceChangeEvent(IEaglerPlayer<PlayerObject> player, EnumVoiceState stateOld, EnumVoiceState stateNew);

}
