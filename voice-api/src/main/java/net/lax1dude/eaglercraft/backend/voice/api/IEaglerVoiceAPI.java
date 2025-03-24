package net.lax1dude.eaglercraft.backend.voice.api;

import java.util.UUID;

public interface IEaglerVoiceAPI<PlayerObject> {

	IVoiceService<PlayerObject> getVoiceService();

	IVoicePlayer<PlayerObject> getEaglerPlayer(PlayerObject player);

	IVoicePlayer<PlayerObject> getEaglerPlayerByName(String playerName);

	IVoicePlayer<PlayerObject> getEaglerPlayerByUUID(UUID playerUUID);

	boolean isEaglerPlayer(PlayerObject player);

	boolean isEaglerPlayerByName(String playerName);

	boolean isEaglerPlayerByUUID(UUID playerUUID);

}
