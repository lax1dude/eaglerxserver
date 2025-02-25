package net.lax1dude.eaglercraft.backend.server.api.rewind;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IEaglerXRewindProtocol<PlayerObject, Attachment> {

	int[] getLegacyProtocols();

	int getEmulatedEaglerHandshake();

	default void handleRegistered(IEaglerXServerAPI<PlayerObject> server) {
	}

	default void handleUnregistered(IEaglerXServerAPI<PlayerObject> server) {
	}

	void initializeConnection(int legacyProtocol, IEaglerXRewindInitializer<Attachment> initializer);

	void handleCreatePlayer(Attachment attachment, IEaglerPlayer<PlayerObject> playerObj);

	void handleDestroyPlayer(Attachment attachment);

}
