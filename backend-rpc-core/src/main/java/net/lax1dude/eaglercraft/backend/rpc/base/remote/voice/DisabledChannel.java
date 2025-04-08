package net.lax1dude.eaglercraft.backend.rpc.base.remote.voice;

import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;

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
