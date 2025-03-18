package net.lax1dude.eaglercraft.backend.server.base.voice;

class ManagedChannel extends VoiceChannel {

	ManagedChannel() {
		
	}

	@Override
	public boolean isManaged() {
		return true;
	}

}