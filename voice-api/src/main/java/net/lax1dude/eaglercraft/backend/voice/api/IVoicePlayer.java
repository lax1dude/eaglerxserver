package net.lax1dude.eaglercraft.backend.voice.api;

import java.util.UUID;

public interface IVoicePlayer<PlayerObject> {

	PlayerObject getPlayerObject();

	boolean isVoiceCapable();

	boolean hasVoiceManager();

	IVoiceManager<PlayerObject> getVoiceManager();

	UUID getUniqueId();

	String getUsername();

}
