package net.lax1dude.eaglercraft.backend.server.base.voice;

import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;

class DisabledChannel implements IVoiceChannel {

	static final DisabledChannel INSTANCE = new DisabledChannel();

	private DisabledChannel() {
	}

	@Override
	public boolean isManaged() {
		return true;
	}

	@Override
	public boolean isDisabled() {
		return true;
	}

}
