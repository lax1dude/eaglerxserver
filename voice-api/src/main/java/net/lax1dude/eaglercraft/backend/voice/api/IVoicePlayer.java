package net.lax1dude.eaglercraft.backend.voice.api;

import java.util.UUID;

public interface IVoicePlayer<PlayerObject> {

	boolean isVoiceSupported();

	IVoiceManager<PlayerObject> getVoiceManager();

	UUID getUniqueId();

	String getUsername();

}
