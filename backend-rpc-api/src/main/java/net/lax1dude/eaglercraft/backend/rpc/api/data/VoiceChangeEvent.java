package net.lax1dude.eaglercraft.backend.rpc.api.data;

import net.lax1dude.eaglercraft.backend.rpc.api.EnumSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;

public final class VoiceChangeEvent implements IRPCEvent {

	public static VoiceChangeEvent create(EnumVoiceState oldState, EnumVoiceState newState) {
		return new VoiceChangeEvent(oldState, newState);
	}

	private final EnumVoiceState oldState;
	private final EnumVoiceState newState;

	private VoiceChangeEvent(EnumVoiceState oldState, EnumVoiceState newState) {
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
		return EnumSubscribeEvents.EVENT_VOICE_CHANGE;
	}

}
