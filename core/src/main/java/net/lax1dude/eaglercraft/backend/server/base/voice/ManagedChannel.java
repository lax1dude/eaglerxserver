package net.lax1dude.eaglercraft.backend.server.base.voice;

class ManagedChannel<PlayerObject> extends VoiceChannel<PlayerObject> {

	ManagedChannel(VoiceServiceLocal<PlayerObject> owner) {
		super(owner);
	}

	@Override
	public boolean isManaged() {
		return true;
	}

}