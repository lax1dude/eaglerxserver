package net.lax1dude.eaglercraft.backend.rpc.adapter;

import java.io.File;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftDestroyPlayerEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftInitializePlayerEvent;

public interface IPlatform<PlayerObject> {

	public interface Init<PlayerObject> {

		IPlatform<PlayerObject> getPlatform();

		void setOnServerEnable(Runnable enable);

		void setOnServerDisable(Runnable disable);

		void setPlayerInitializer(IBackendRPCPlayerInitializer<?, ?, PlayerObject> initializer);

		InitLocalMode<PlayerObject> localMode();

		InitRemoteMode<PlayerObject> remoteMode();

	}

	public interface InitLocalMode<PlayerObject> {

		void setOnEaglerPlayerInitialized(Consumer<IEaglercraftInitializePlayerEvent<PlayerObject>> handler);

		void setOnEaglerPlayerDestroyed(Consumer<IEaglercraftDestroyPlayerEvent<PlayerObject>> handler);

	}

	public interface InitRemoteMode<PlayerObject> {

		void setEaglerPlayerChannels(Collection<IBackendRPCMessageChannel<PlayerObject>> channels);

	}

	EnumAdapterPlatformType getType();

	Class<PlayerObject> getPlayerClass();

	String getPluginId();

	File getDataFolder();

	IPlatformLogger logger();

	void forEachPlayer(Consumer<IPlatformPlayer<PlayerObject>> playerCallback);

	IPlatformPlayer<PlayerObject> getPlayer(PlayerObject playerObj);

	IPlatformPlayer<PlayerObject> getPlayer(String username);

	IPlatformPlayer<PlayerObject> getPlayer(UUID uuid);

	Collection<IPlatformPlayer<PlayerObject>> getAllPlayers();

	IPlatformScheduler getScheduler();

}
