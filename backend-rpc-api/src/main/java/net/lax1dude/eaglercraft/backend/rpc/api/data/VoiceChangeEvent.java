package net.lax1dude.eaglercraft.backend.rpc.api.data;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.EnumSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;

public final class VoiceChangeEvent implements IRPCEvent {

	@Nonnull
	public static VoiceChangeEvent create(@Nonnull EnumVoiceState oldState, @Nonnull EnumVoiceState newState) {
		if(oldState == null) {
			throw new NullPointerException("oldState");
		}
		if(newState == null) {
			throw new NullPointerException("newState");
		}
		return new VoiceChangeEvent(oldState, newState);
	}

	private final EnumVoiceState oldState;
	private final EnumVoiceState newState;

	private VoiceChangeEvent(EnumVoiceState oldState, EnumVoiceState newState) {
		this.oldState = oldState;
		this.newState = newState;
	}

	@Nonnull
	public EnumVoiceState getOldState() {
		return oldState;
	}

	@Nonnull
	public EnumVoiceState getNewState() {
		return newState;
	}

	@Nonnull
	@Override
	public EnumSubscribeEvents getEventType() {
		return EnumSubscribeEvents.EVENT_VOICE_CHANGE;
	}

}
