package net.lax1dude.eaglercraft.backend.server.base.voice;

import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;

public class VoiceChannel implements IVoiceChannel {

	@Override
	public boolean isManaged() {
		return false;
	}

	@Override
	public boolean isDisabled() {
		return false;
	}

}
