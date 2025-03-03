package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeHolder;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistry;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.EaglerXServerAPIFactory;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.IEaglerAPIFactory;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationService;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuService;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryServer;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindService;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinService;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorService;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IWebServer;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public interface IEaglerXServerAPI<PlayerObject> extends IAttributeHolder {

	public static <PlayerObject> IEaglerXServerAPI<PlayerObject> instance(Class<PlayerObject> playerObj) {
		return EaglerXServerAPIFactory.INSTANCE.createAPI(playerObj);
	}

	public static IEaglerXServerAPI<?> instance() {
		EaglerXServerAPIFactory factory = EaglerXServerAPIFactory.INSTANCE;
		return factory.createAPI(factory.getPlayerClass());
	}

	public static Class<?> getPlayerClass() {
		return EaglerXServerAPIFactory.INSTANCE.getPlayerClass();
	}

	public static IEaglerAPIFactory getFactoryInstance() {
		return EaglerXServerAPIFactory.INSTANCE;
	}

	IEaglerAPIFactory getFactory();

	EnumPlatformType getPlatformType();

	Class<?> getEaglerXServerClass();

	<ServerImpl> ServerImpl getEaglerXServerInstance(Class<ServerImpl> clazz);

	Class<?> getPlatformPluginClass();

	<PluginImpl> PluginImpl getPlatformPluginInstance(Class<PluginImpl> clazz);

	String getServerBrand();

	String getServerVersion();

	String getServerName();

	UUID getServerUUID();

	boolean isAuthenticationEventsEnabled();

	boolean isEaglerHandshakeSupported(int vers);

	boolean isEaglerProtocolSupported(GamePluginMessageProtocol vers);

	boolean isMinecraftProtocolSupported(int vers);

	IBrandRegistry getBrandRegistry();

	IBasePlayer<PlayerObject> getPlayer(PlayerObject player);

	IBasePlayer<PlayerObject> getPlayerByName(String playerName);

	IBasePlayer<PlayerObject> getPlayerByUUID(UUID playerUUID);

	IEaglerPlayer<PlayerObject> getEaglerPlayer(PlayerObject player);

	IEaglerPlayer<PlayerObject> getEaglerPlayerByName(String playerName);

	IEaglerPlayer<PlayerObject> getEaglerPlayerByUUID(UUID playerUUID);

	boolean isEaglerPlayer(PlayerObject player);

	boolean isEaglerPlayerByName(String playerName);

	boolean isEaglerPlayerByUUID(UUID playerUUID);

	void forEachPlayer(Consumer<IBasePlayer<PlayerObject>> callback);

	void forEachEaglerPlayer(Consumer<IEaglerPlayer<PlayerObject>> callback);

	Collection<IBasePlayer<PlayerObject>> getAllPlayers();

	Set<IEaglerPlayer<PlayerObject>> getAllEaglerPlayers();

	int getEaglerPlayerCount();

	Collection<byte[]> getUpdateCertificates();

	Collection<IEaglerListenerInfo> getAllEaglerListeners();

	IEaglerListenerInfo getListenerByName(String name);

	IEaglerListenerInfo getListenerByAddress(SocketAddress address);

	ISkinService<PlayerObject> getSkinService();

	IVoiceService<PlayerObject> getVoiceService();

	INotificationService<PlayerObject> getNotificationService();

	IPauseMenuService<PlayerObject> getPauseMenuService();

	IWebViewService<PlayerObject> getWebViewService();

	ISupervisorService<PlayerObject> getSupervisorService();

	IEaglerXRewindService<PlayerObject> getEaglerXRewindService();

	IPacketImageLoader getPacketImageLoader();

	IServerIconLoader getServerIconLoader();

	IQueryServer getQueryServer();

	IWebServer getWebServer();

	IScheduler getScheduler();

	IBinaryHTTPClient getBinaryHTTPClient();

	INativeZlib createNativeZlib(boolean compression, boolean decompression, int compressionLevel);

	IAttributeManager getAttributeManager();

	NettyUnsafe getNettyUnsafe();

	public interface NettyUnsafe {

		ChannelFactory<? extends Channel> getChannelFactory(SocketAddress address);

		ChannelFactory<? extends ServerChannel> getServerChannelFactory(SocketAddress address);

		EventLoopGroup getBossEventLoopGroup();

		EventLoopGroup getWorkerEventLoopGroup();

	}

}
