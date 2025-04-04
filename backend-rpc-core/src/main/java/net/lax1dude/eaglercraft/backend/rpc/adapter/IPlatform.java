package net.lax1dude.eaglercraft.backend.rpc.adapter;

import java.io.File;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.rpc.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent;

public interface IPlatform<PlayerObject> {

	public interface Init<PlayerObject> {

		IPlatform<PlayerObject> getPlatform();

		void setOnServerEnable(Runnable enable);

		void setOnServerDisable(Runnable disable);

		void setPlayerInitializer(IBackendRPCPlayerInitializer<?, PlayerObject> initializer);

		InitLocalMode<PlayerObject> localMode();

		InitRemoteMode<PlayerObject> remoteMode();

	}

	public interface InitLocalMode<PlayerObject> {

		void setOnWebViewChannel(Consumer<IEaglercraftWebViewChannelEvent<PlayerObject>> handler);

		void setOnWebViewMessage(Consumer<IEaglercraftWebViewMessageEvent<PlayerObject>> handler);

		void setOnVoiceChange(Consumer<IEaglercraftVoiceChangeEvent<PlayerObject>> handler);

	}

	public interface InitRemoteMode<PlayerObject> {

		void setEaglerPlayerChannels(Collection<IBackendRPCMessageChannel<PlayerObject>> channels);

	}

	EnumAdapterPlatformType getType();

	Class<PlayerObject> getPlayerClass();

	String getPluginId();

	File getDataFolder();

	IPlatformLogger logger();

	IEventDispatchAdapter<PlayerObject> eventDispatcher();

	void forEachPlayer(Consumer<IPlatformPlayer<PlayerObject>> playerCallback);

	IPlatformPlayer<PlayerObject> getPlayer(PlayerObject playerObj);

	IPlatformPlayer<PlayerObject> getPlayer(String username);

	IPlatformPlayer<PlayerObject> getPlayer(UUID uuid);

	Collection<IPlatformPlayer<PlayerObject>> getAllPlayers();

	IPlatformScheduler getScheduler();

}
