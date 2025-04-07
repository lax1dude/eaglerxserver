package net.lax1dude.eaglercraft.backend.rpc.base.remote.voice;

class ManagedChannel<PlayerObject> extends VoiceChannel<PlayerObject> {

	ManagedChannel(VoiceServiceRemote<PlayerObject> owner) {
		super(owner);
	}

	@Override
	public boolean isManaged() {
		return true;
	}

}