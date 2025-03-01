package net.lax1dude.eaglercraft.backend.server.adapter;

import java.util.Collection;
import java.util.function.Consumer;

public interface IPlatformServer<PlayerObject> {

	boolean isEaglerRegistered();

	String getServerConfName();

	Collection<IPlatformPlayer<PlayerObject>> getAllPlayers();

	void forEachPlayer(Consumer<IPlatformPlayer<PlayerObject>> callback);

}
