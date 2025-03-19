package net.lax1dude.eaglercraft.backend.server.base.voice;

class ManagedChannel<PlayerObject> extends VoiceChannel<PlayerObject> {

	ManagedChannel(VoiceService<PlayerObject> owner) {
		super(owner);
	}

	@Override
	public boolean isManaged() {
		return true;
	}

}