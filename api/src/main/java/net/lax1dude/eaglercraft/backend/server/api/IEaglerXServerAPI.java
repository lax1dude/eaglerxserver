package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeHolder;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandService;
import net.lax1dude.eaglercraft.backend.server.api.collect.HPPC;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.EaglerXServerAPIFactory;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.IEaglerAPIFactory;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTHelper;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationService;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuService;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryServer;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindService;
import net.lax1dude.eaglercraft.backend.server.api.skins.IProfileResolver;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinService;
import net.lax1dude.eaglercraft.backend.server.api.skins.TexturesProperty;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorService;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IWebServer;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public interface IEaglerXServerAPI<PlayerObject> extends IAttributeHolder {

	@Nonnull
	public static <PlayerObject> IEaglerXServerAPI<PlayerObject> instance(@Nonnull Class<PlayerObject> playerObj) {
		return EaglerXServerAPIFactory.INSTANCE.getAPI(playerObj);
	}

	@Nonnull
	public static IEaglerXServerAPI<?> instance() {
		return EaglerXServerAPIFactory.INSTANCE.getDefaultAPI();
	}

	@Nonnull
	public static Set<Class<?>> getPlayerTypes() {
		return EaglerXServerAPIFactory.INSTANCE.getPlayerTypes();
	}

	@Nonnull
	public static IEaglerAPIFactory getFactoryInstance() {
		return EaglerXServerAPIFactory.INSTANCE;
	}

	@Nonnull
	IEaglerAPIFactory getFactory();

	@Nonnull
	EnumPlatformType getPlatformType();

	@Nonnull
	Class<PlayerObject> getPlayerClass();

	@Nonnull
	String getServerBrand();

	@Nonnull
	String getServerVersion();

	@Nonnull
	String getServerName();

	@Nonnull
	UUID getServerUUID();

	boolean isAuthenticationEventsEnabled();

	boolean isEaglerHandshakeSupported(int vers);

	boolean isEaglerProtocolSupported(@Nonnull GamePluginMessageProtocol vers);

	boolean isMinecraftProtocolSupported(int vers);

	@Nullable
	IBasePlayer<PlayerObject> getPlayer(@Nonnull PlayerObject player);

	@Nullable
	IBasePlayer<PlayerObject> getPlayerByName(@Nonnull String playerName);

	@Nullable
	IBasePlayer<PlayerObject> getPlayerByUUID(@Nonnull UUID playerUUID);

	@Nullable
	IEaglerPlayer<PlayerObject> getEaglerPlayer(@Nonnull PlayerObject player);

	@Nullable
	IEaglerPlayer<PlayerObject> getEaglerPlayerByName(@Nonnull String playerName);

	@Nullable
	IEaglerPlayer<PlayerObject> getEaglerPlayerByUUID(@Nonnull UUID playerUUID);

	boolean isPlayer(@Nonnull PlayerObject player);

	boolean isPlayerByName(@Nonnull String playerName);

	boolean isPlayerByUUID(@Nonnull UUID playerUUID);

	boolean isEaglerPlayer(@Nonnull PlayerObject player);

	boolean isEaglerPlayerByName(@Nonnull String playerName);

	boolean isEaglerPlayerByUUID(@Nonnull UUID playerUUID);

	void forEachPlayer(@Nonnull Consumer<IBasePlayer<PlayerObject>> callback);

	void forEachEaglerPlayer(@Nonnull Consumer<IEaglerPlayer<PlayerObject>> callback);

	@Nonnull
	Collection<IBasePlayer<PlayerObject>> getAllPlayers();

	@Nonnull
	Collection<IEaglerPlayer<PlayerObject>> getAllEaglerPlayers();

	int getEaglerPlayerCount();

	@Nonnull
	Collection<IUpdateCertificate> getUpdateCertificates();

	@Nonnull
	default IUpdateCertificate createUpdateCertificate(@Nonnull byte[] data) {
		return createUpdateCertificate(data, 0, data.length);
	}

	@Nonnull
	IUpdateCertificate createUpdateCertificate(@Nonnull byte[] data, int offset, int length);

	void addUpdateCertificate(@Nonnull IUpdateCertificate cert);

	@Nonnull
	Collection<IEaglerListenerInfo> getAllEaglerListeners();

	@Nullable
	IEaglerListenerInfo getListenerByName(@Nonnull String name);

	@Nullable
	IEaglerListenerInfo getListenerByAddress(@Nonnull SocketAddress address);

	@Nonnull
	IProfileResolver getProfileResolver();

	@Nullable
	TexturesProperty getEaglerPlayersVanillaSkin();

	void setEaglerPlayersVanillaSkin(@Nullable TexturesProperty property);

	boolean isEaglerPlayerPropertyEnabled();

	void setEaglerPlayerProperyEnabled(boolean enable);

	void registerExtendedCapability(@Nonnull Object plugin, @Nonnull ExtendedCapabilitySpec capability);

	void unregisterExtendedCapability(@Nonnull Object plugin, @Nonnull ExtendedCapabilitySpec capability);

	boolean isExtendedCapabilityRegistered(@Nonnull UUID capabilityUUID, int version);

	@Nonnull
	ISkinService<PlayerObject> getSkinService();

	@Nonnull
	IVoiceService<PlayerObject> getVoiceService();

	@Nonnull
	IBrandService<PlayerObject> getBrandService();

	@Nonnull
	INotificationService<PlayerObject> getNotificationService();

	@Nonnull
	IPauseMenuService<PlayerObject> getPauseMenuService();

	@Nonnull
	IWebViewService<PlayerObject> getWebViewService();

	@Nonnull
	ISupervisorService<PlayerObject> getSupervisorService();

	@Nonnull
	IEaglerXRewindService<PlayerObject> getEaglerXRewindService();

	@Nonnull
	IPacketImageLoader getPacketImageLoader();

	@Nonnull
	IServerIconLoader getServerIconLoader();

	@Nonnull
	IQueryServer getQueryServer();

	@Nonnull
	IWebServer getWebServer();

	@Nonnull
	IScheduler getScheduler();

	@Nonnull
	Set<Class<?>> getComponentTypes();

	@Nonnull
	<ComponentObject> IComponentSerializer<ComponentObject> getComponentSerializer(
			@Nonnull Class<ComponentObject> componentType);

	@Nonnull
	IComponentHelper getComponentHelper();

	@Nonnull
	INBTHelper getNBTHelper();

	@Nonnull
	IBinaryHTTPClient getBinaryHTTPClient();

	@Nonnull
	UUID intern(@Nonnull UUID uuid);

	@Nonnull
	IAttributeManager getAttributeManager();

	@Nonnull
	HPPC getHPPC();

	boolean isNettyPlatform();

	@Nonnull
	NettyUnsafe netty();

	public interface NettyUnsafe {

		@Nonnull
		ChannelFactory<? extends Channel> getChannelFactory(@Nullable SocketAddress address);

		@Nonnull
		ChannelFactory<? extends ServerChannel> getServerChannelFactory(@Nullable SocketAddress address);

		@Nullable
		EventLoopGroup getBossEventLoopGroup();

		@Nonnull
		EventLoopGroup getWorkerEventLoopGroup();

	}

}
