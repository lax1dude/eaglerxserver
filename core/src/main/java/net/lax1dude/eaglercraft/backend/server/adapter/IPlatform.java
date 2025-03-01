package net.lax1dude.eaglercraft.backend.server.adapter;

import java.io.File;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.config.EnumConfigFormat;

public interface IPlatform<PlayerObject> {

	public interface Init<PlayerObject> {

		void setOnServerEnable(Runnable enable);

		void setOnServerDisable(Runnable disable);

		void setEaglerPlayerChannels(Collection<IEaglerXServerMessageChannel> channels);

		void setPipelineInitializer(IEaglerXServerNettyPipelineInitializer<?> initializer);

		void setConnectionInitializer(IEaglerXServerConnectionInitializer<?, ?> initializer);

		void setPlayerInitializer(IEaglerXServerPlayerInitializer<?, ?, PlayerObject> initializer);

		void setCommandRegistry(Collection<IEaglerXServerCommandType<PlayerObject>> commands);

		IPlatform<PlayerObject> getPlatform();

	}

	public interface InitProxying<PlayerObject> extends Init<PlayerObject> {

		void setEaglerListeners(Collection<IEaglerXServerListener> listeners);

		void setEaglerBackendChannels(Collection<IEaglerXServerMessageChannel> channels);

	}

	public interface InitNonProxying<PlayerObject> extends Init<PlayerObject> {

		void setEaglerListener(IEaglerXServerListener listener);

		SocketAddress getListenerAddress();

	}

	EnumAdapterPlatformType getType();

	Class<PlayerObject> getPlayerClass();

	String getPluginId();

	File getDataFolder();

	IPlatformLogger logger();

	IPlatformCommandSender<PlayerObject> getConsole();

	void forEachPlayer(Consumer<IPlatformPlayer<PlayerObject>> playerCallback);

	IPlatformPlayer<PlayerObject> getPlayer(PlayerObject playerObj);

	IPlatformPlayer<PlayerObject> getPlayer(String username);

	IPlatformPlayer<PlayerObject> getPlayer(UUID uuid);

	Collection<IPlatformPlayer<PlayerObject>> getAllPlayers();

	Map<String, IPlatformServer<PlayerObject>> getRegisteredServers();

	IEventDispatchAdapter<PlayerObject, ?> eventDispatcher();

	IPlatformScheduler getScheduler();

	Set<EnumConfigFormat> getConfigFormats();

	IPlatformComponentHelper getComponentHelper();

	boolean isOnlineMode();

	int getPlayerTotal();

	int getPlayerMax();

	void handleConnectionInitFallback(Channel channel);

	void handleUndoCompression(ChannelHandlerContext ctx);

}
