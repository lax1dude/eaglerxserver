package net.lax1dude.eaglercraft.backend.rpc.api.data;

import net.lax1dude.eaglercraft.backend.rpc.api.EnumSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;

public final class ToggledVoiceEvent implements IRPCEvent {

	public static ToggledVoiceEvent create(EnumVoiceState oldState, EnumVoiceState newState) {
		return new ToggledVoiceEvent(oldState, newState);
	}

	private final EnumVoiceState oldState;
	private final EnumVoiceState newState;

	private ToggledVoiceEvent(EnumVoiceState oldState, EnumVoiceState newState) {
		this.oldState = oldState;
		this.newState = newState;
	}

	public EnumVoiceState getOldState() {
		return oldState;
	}

	public EnumVoiceState getNewState() {
		return newState;
	}

	@Override
	public EnumSubscribeEvents getEventType() {
		return EnumSubscribeEvents.EVENT_TOGGLE_VOICE;
	}

}
