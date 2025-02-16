package net.lax1dude.eaglercraft.backend.server.adapter;

import java.io.File;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;

public interface IPlatform<PlayerObject> {

	public interface Init<PlayerObject> {

		void setOnServerEnable(Runnable enable);

		void setOnServerDisable(Runnable disable);

		void setEaglerPlayerChannels(Collection<IEaglerXServerMessageChannel> channels);

		void setPipelineInitializer(IEaglerXServerNettyPipelineInitializer<?> initializer);

		void setConnectionInitializer(IEaglerXServerConnectionInitializer<?, ?> initializer);

		void setPlayerInitializer(IEaglerXServerPlayerInitializer<?, ?, PlayerObject> initializer);

		void setCommandRegistry(Collection<IEaglerXServerCommandType> commands);

		IPlatform<PlayerObject> getPlatform();

	}

	public interface InitProxying<PlayerObject> extends Init<PlayerObject> {

		void setEaglerListeners(Collection<IEaglerXServerListener> listeners);

		void setEaglerBackendChannels(Collection<IEaglerXServerMessageChannel> channels);

	}

	public interface InitNonProxying<PlayerObject> extends Init<PlayerObject> {

		void setEaglerListener(IEaglerXServerListener listener);

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

	IEventDispatchAdapter<PlayerObject, ?> eventDispatcher();

}
