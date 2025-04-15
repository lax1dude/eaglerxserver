package net.lax1dude.eaglercraft.backend.server.api.rewind;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IEaglerXRewindProtocol<PlayerObject, Attachment> {

	@Nonnull
	int[] getLegacyProtocols();

	int getEmulatedEaglerHandshake();

	default void handleRegistered(@Nonnull IEaglerXServerAPI<PlayerObject> server) {
	}

	default void handleUnregistered(@Nonnull IEaglerXServerAPI<PlayerObject> server) {
	}

	void initializeConnection(int legacyProtocol, @Nonnull IEaglerXRewindInitializer<Attachment> initializer);

	void handleCreatePlayer(@Nullable Attachment attachment, @Nonnull IEaglerPlayer<PlayerObject> playerObj);

	void handleDestroyPlayer(@Nullable Attachment attachment);

}
