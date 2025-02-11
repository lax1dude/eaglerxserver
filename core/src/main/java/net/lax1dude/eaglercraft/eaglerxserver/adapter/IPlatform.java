package net.lax1dude.eaglercraft.eaglerxserver.adapter;

import java.io.File;
import java.util.Collection;

public interface IPlatform {

	public interface Init {

		void setOnServerEnable(Runnable enable);

		void setOnServerDisable(Runnable disable);

		void setEaglerPlayerChannels(Collection<IEaglerXServerMessageChannel> channels);

		void setPipelineInitializer(IEaglerXServerPipelineInitializer<?> initializer);

		void setPlayerInitializer(IEaglerXServerPlayerInitializer<?> initializer);

		void setCommandRegistry(Collection<IEaglerXServerCommandType> commands);

		IPlatform getPlatform();

	}

	public interface InitProxying extends Init {

		void setEaglerListeners(Collection<IEaglerXServerListener> listeners);

		void setEaglerBackendChannels(Collection<IEaglerXServerMessageChannel> channels);

	}

	public interface InitNonProxying extends Init {

		void setEaglerListener(IEaglerXServerListener listener);

	}

	EnumPlatformType getType();

	String getPluginName();

	File getDataFolder();

	IPlatformLogger getLogger();

	Collection<IPlatformPlayer> getPlayers();

}
