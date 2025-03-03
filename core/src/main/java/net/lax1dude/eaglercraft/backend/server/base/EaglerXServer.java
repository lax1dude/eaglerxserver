package net.lax1dude.eaglercraft.backend.server.base;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import javax.net.ssl.SSLException;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import net.lax1dude.eaglercraft.backend.server.adapter.AbortLoadException;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerImpl;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatform;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentHelper;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformZlib;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.api.EnumPlatformType;
import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.IBinaryHTTPClient;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.INativeZlib;
import net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.backend.server.api.IScheduler;
import net.lax1dude.eaglercraft.backend.server.api.IServerIconLoader;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.IEaglerAPIFactory;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationService;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuService;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindProtocol;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindService;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinService;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorService;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;
import net.lax1dude.eaglercraft.backend.server.base.command.CommandBrand;
import net.lax1dude.eaglercraft.backend.server.base.command.CommandConfirmCode;
import net.lax1dude.eaglercraft.backend.server.base.command.CommandDomain;
import net.lax1dude.eaglercraft.backend.server.base.command.CommandProtocol;
import net.lax1dude.eaglercraft.backend.server.base.command.CommandUserAgent;
import net.lax1dude.eaglercraft.backend.server.base.command.CommandVersion;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataListener;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataRoot;
import net.lax1dude.eaglercraft.backend.server.base.config.EaglerConfigLoader;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.PipelineTransformer;
import net.lax1dude.eaglercraft.backend.skin_cache.HTTPClient;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class EaglerXServer<PlayerObject> implements IEaglerXServerImpl<PlayerObject>, IEaglerAPIFactory,
		IEaglerXServerAPI<PlayerObject>, IEaglerXServerAPI.NettyUnsafe {

	private final EaglerAttributeManager attributeManager = APIFactoryImpl.INSTANCE.getEaglerAttribManager();
	private final EaglerAttributeManager.EaglerAttributeHolder attributeHolder = attributeManager.createEaglerHolder();

	private boolean hasStartedLoading = false;
	private IPlatform<PlayerObject> platform;
	private Class<?> platformClazz;
	private EnumPlatformType platformType;
	private Class<PlayerObject> playerClazz;
	private ConfigDataRoot config;
	private IEventDispatchAdapter<PlayerObject, ?> eventDispatcher;
	private Set<EaglerPlayerInstance<PlayerObject>> eaglerPlayers;
	private BrandRegistry brandRegistry;
	private Map<String, EaglerListener> listeners;
	private Map<SocketAddress, EaglerListener> listenersByAddress;
	private QueryServer queryServer;
	private WebServer webServer;
	private RewindService<PlayerObject> rewindService;
	private PipelineTransformer pipelineTransformer;
	private SSLCertificateManager certificateManager;
	private Scheduler scheduler;
	private String serverListConfirmCode;
	private HTTPClient httpClient;
	private BinaryHTTPClient httpClientAPI;

	public EaglerXServer() {
	}

	@Override
	public void load(IPlatform.Init<PlayerObject> init) {
		if(hasStartedLoading) {
			throw new IllegalStateException();
		}
		hasStartedLoading = true;
		eaglerPlayers = Sets.newConcurrentHashSet();
		platform = init.getPlatform();
		platformClazz = platform.getClass();
		playerClazz = platform.getPlayerClass();
		switch(platform.getType()) {
		case BUNGEE: platformType = EnumPlatformType.BUNGEECORD; break;
		case BUKKIT: platformType = EnumPlatformType.BUKKIT; break;
		case VELOCITY: platformType = EnumPlatformType.VELOCITY; break;
		default: platformType = EnumPlatformType.STANDALONE; break;
		}
		
		logger().info("Loading " + getServerBrand() + " " + getServerVersion() + "...");
		logger().info("(Platform: " + platformType.getName() + ")");
		
		if(platform.isOnlineMode()) {
			throw new AbortLoadException("Online mode is not supported yet!");
		}
		
		eventDispatcher = platform.eventDispatcher();
		
		try {
			config = EaglerConfigLoader.loadConfig(platform);
		} catch (IOException e) {
			throw new AbortLoadException("Could not read one or more config files!", e);
		}
		
		logger().info("Server Name: \"" + config.getSettings().getServerName() + "\"");
		
		brandRegistry = new BrandRegistry();
		queryServer = new QueryServer(this);
		webServer = new WebServer(this);
		rewindService = new RewindService<PlayerObject>(this);
		pipelineTransformer = new PipelineTransformer(this, rewindService);
		certificateManager = new SSLCertificateManager(logger());
		scheduler = new Scheduler(platform.getScheduler());
		httpClient = new HTTPClient(platform.getWorkerEventLoopGroup(), platform.getChannelFactory(null),
				"Mozilla/5.0 " + getServerVersionString());
		httpClientAPI = new BinaryHTTPClient(httpClient);
		
		init.setOnServerEnable(this::enableHandler);
		init.setOnServerDisable(this::disableHandler);
		init.setPipelineInitializer(new EaglerXServerNettyPipelineInitializer<PlayerObject>(this));
		init.setConnectionInitializer(new EaglerXServerConnectionInitializer<PlayerObject>(this));
		init.setPlayerInitializer(new EaglerXServerPlayerInitializer<PlayerObject>(this));
		init.setCommandRegistry(Arrays.asList(
				new CommandVersion<PlayerObject>(this),
				new CommandBrand<PlayerObject>(this),
				new CommandProtocol<PlayerObject>(this),
				new CommandDomain<PlayerObject>(this),
				new CommandUserAgent<PlayerObject>(this),
				new CommandConfirmCode<PlayerObject>(this)
		));
		
		if(platform.getType().proxy) {
			loadProxying((IPlatform.InitProxying<PlayerObject>)init);
		}else {
			loadNonProxying((IPlatform.InitNonProxying<PlayerObject>)init);
		}
		
		eventDispatcher.setAPI(this);
		APIFactoryImpl.INSTANCE.initialize(playerClazz, this);
	}

	private void loadProxying(IPlatform.InitProxying<PlayerObject> init) {
		ImmutableMap.Builder<String, EaglerListener> listenersBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<SocketAddress, EaglerListener> listenersByAddressBuilder = ImmutableMap.builder();
		ImmutableList.Builder<IEaglerXServerListener> listenersImpl = ImmutableList.builder();
		for(ConfigDataListener listener : config.getListeners().values()) {
			EaglerListener eagListener;
			try {
				eagListener = new EaglerListener(this, listener);
			}catch(SSLException ex) {
				throw new AbortLoadException("TLS configuration is invalid!", ex);
			}
			listenersBuilder.put(listener.getListenerName(), eagListener);
			listenersByAddressBuilder.put(listener.getInjectAddress(), eagListener);
			listenersImpl.add(eagListener);
		}
		listeners = listenersBuilder.build();
		listenersByAddress = listenersByAddressBuilder.build();
		init.setEaglerListeners(listenersImpl.build());
	}

	private void loadNonProxying(IPlatform.InitNonProxying<PlayerObject> init) {
		EaglerListener eagListener;
		try {
			eagListener = new EaglerListener(this, init.getListenerAddress(),
					config.getListeners().values().iterator().next());
		}catch(SSLException ex) {
			throw new AbortLoadException("TLS configuration is invalid!", ex);
		}
		listeners = ImmutableMap.of("default", eagListener);
		listenersByAddress = ImmutableMap.of(init.getListenerAddress(), eagListener);
		init.setEaglerListener(eagListener);
	}

	public ConfigDataRoot getConfig() {
		return config;
	}

	public IPlatform<PlayerObject> getPlatform() {
		return platform;
	}

	public PipelineTransformer getPipelineTransformer() {
		return pipelineTransformer;
	}

	public SSLCertificateManager getCertificateManager() {
		return certificateManager;
	}

	public void enableHandler() {
		logger().info("Enabling " + getServerBrand() + " " + getServerVersion() + "...");
		
	}

	public void disableHandler() {
		
	}

	public void registerPlayer(BasePlayerInstance<PlayerObject> playerInstance) {
		
	}

	public void registerEaglerPlayer(EaglerPlayerInstance<PlayerObject> playerInstance) {
		if(!eaglerPlayers.add(playerInstance)) return;
		EaglerConnectionInstance pendingConnection = playerInstance.connectionImpl();
		NettyPipelineData.ProfileDataHolder profileData = pendingConnection.transferProfileData();
		
		//TODO: handle profile

		if(pendingConnection.isEaglerXRewindPlayer()) {
			((IEaglerXRewindProtocol<PlayerObject, Object>) pendingConnection.getRewindProtocol())
					.handleCreatePlayer(pendingConnection.getRewindAttachment(), playerInstance);
		}

		eventDispatcher.dispatchInitializePlayerEvent(playerInstance, null);
	}

	public void unregisterPlayer(BasePlayerInstance<PlayerObject> playerInstance) {
		
	}

	public void unregisterEaglerPlayer(EaglerPlayerInstance<PlayerObject> playerInstance) {
		if(!eaglerPlayers.remove(playerInstance)) return;
		EaglerConnectionInstance pendingConnection = playerInstance.connectionImpl();

		if(pendingConnection.isEaglerXRewindPlayer()) {
			((IEaglerXRewindProtocol<PlayerObject, Object>) pendingConnection.getRewindProtocol())
					.handleDestroyPlayer(pendingConnection.getRewindAttachment());
		}

		eventDispatcher.dispatchDestroyPlayerEvent(playerInstance, null);
	}

	@Override
	public Class<PlayerObject> getPlayerClass() {
		return playerClazz;
	}

	@Override
	public IAttributeManager getGlobalAttributeManager() {
		return attributeManager;
	}

	public EaglerAttributeManager getEaglerAttribManager() {
		return attributeManager;
	}

	@Override
	public <T> IEaglerXServerAPI<T> createAPI(Class<T> playerClass) {
		if(playerClazz != playerClass) {
			throw new ClassCastException("Class " + playerClazz.getName() + " cannot be cast to " + playerClass.getName());
		}
		return (IEaglerXServerAPI<T>) this;
	}

	@Override
	public <T> T get(IAttributeKey<T> key) {
		return attributeHolder.get(key);
	}

	@Override
	public <T> void set(IAttributeKey<T> key, T value) {
		attributeHolder.set(key, value);
	}

	@Override
	public IEaglerAPIFactory getFactory() {
		return this;
	}

	@Override
	public Class<?> getEaglerXServerClass() {
		return EaglerXServer.class;
	}

	@Override
	public <ServerImpl> ServerImpl getEaglerXServerInstance(Class<ServerImpl> clazz) {
		if(EaglerXServer.class != clazz) {
			throw new ClassCastException("Class " + EaglerXServer.class.getName() + " cannot be cast to " + clazz.getName());
		}
		return (ServerImpl) this;
	}

	@Override
	public EnumPlatformType getPlatformType() {
		return platformType;
	}

	@Override
	public Class<?> getPlatformPluginClass() {
		return platformClazz;
	}

	@Override
	public <PluginImpl> PluginImpl getPlatformPluginInstance(Class<PluginImpl> clazz) {
		if(platformClazz != clazz) {
			throw new ClassCastException("Class " + platformClazz.getName() + " cannot be cast to " + clazz.getName());
		}
		return (PluginImpl) platform;
	}

	@Override
	public String getServerBrand() {
		return EaglerXServerVersion.BRAND;
	}

	@Override
	public String getServerVersion() {
		return EaglerXServerVersion.VERSION;
	}

	public String getServerVersionString() {
		return EaglerXServerVersion.BRAND + "/" + EaglerXServerVersion.VERSION;
	}

	@Override
	public String getServerName() {
		return config.getSettings().getServerName();
	}

	@Override
	public UUID getServerUUID() {
		return config.getSettings().getServerUUID();
	}

	public String getServerUUIDString() {
		return config.getSettings().getServerUUIDString();
	}

	@Override
	public boolean isAuthenticationEventsEnabled() {
		return config.getSettings().isEnableAuthenticationEvents();
	}

	@Override
	public boolean isEaglerHandshakeSupported(int vers) {
		return config.getSettings().getProtocols().isEaglerHandshakeSupported(vers);
	}

	@Override
	public boolean isEaglerProtocolSupported(GamePluginMessageProtocol vers) {
		return config.getSettings().getProtocols().isEaglerProtocolSupported(vers.ver);
	}

	@Override
	public boolean isMinecraftProtocolSupported(int vers) {
		return config.getSettings().getProtocols().isMinecraftProtocolSupported(vers);
	}

	@Override
	public BrandRegistry getBrandRegistry() {
		return brandRegistry;
	}

	@Override
	public BasePlayerInstance<PlayerObject> getPlayer(PlayerObject player) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(player);
		return platformPlayer != null ? platformPlayer.getPlayerAttachment() : null;
	}

	@Override
	public BasePlayerInstance<PlayerObject> getPlayerByName(String playerName) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerName);
		return platformPlayer != null ? platformPlayer.getPlayerAttachment() : null;
	}

	@Override
	public BasePlayerInstance<PlayerObject> getPlayerByUUID(UUID playerUUID) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerUUID);
		return platformPlayer != null ? platformPlayer.getPlayerAttachment() : null;
	}

	@Override
	public EaglerPlayerInstance<PlayerObject> getEaglerPlayer(PlayerObject player) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(player);
		if(platformPlayer != null) {
			IBasePlayer<PlayerObject> basePlayer = platformPlayer.getPlayerAttachment();
			if(basePlayer.isEaglerPlayer()) {
				return (EaglerPlayerInstance<PlayerObject>) basePlayer;
			}
		}
		return null;
	}

	@Override
	public EaglerPlayerInstance<PlayerObject> getEaglerPlayerByName(String playerName) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerName);
		if(platformPlayer != null) {
			IBasePlayer<PlayerObject> basePlayer = platformPlayer.getPlayerAttachment();
			if(basePlayer.isEaglerPlayer()) {
				return (EaglerPlayerInstance<PlayerObject>) basePlayer;
			}
		}
		return null;
	}

	@Override
	public EaglerPlayerInstance<PlayerObject> getEaglerPlayerByUUID(UUID playerUUID) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerUUID);
		if(platformPlayer != null) {
			IBasePlayer<PlayerObject> basePlayer = platformPlayer.getPlayerAttachment();
			if(basePlayer.isEaglerPlayer()) {
				return (EaglerPlayerInstance<PlayerObject>) basePlayer;
			}
		}
		return null;
	}

	@Override
	public boolean isEaglerPlayer(PlayerObject player) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(player);
		return platformPlayer != null && platformPlayer.<IBasePlayer<PlayerObject>>getConnectionAttachment().isEaglerPlayer();
	}

	@Override
	public boolean isEaglerPlayerByName(String playerName) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerName);
		return platformPlayer != null && platformPlayer.<IBasePlayer<PlayerObject>>getConnectionAttachment().isEaglerPlayer();
	}

	@Override
	public boolean isEaglerPlayerByUUID(UUID playerUUID) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerUUID);
		return platformPlayer != null && platformPlayer.<IBasePlayer<PlayerObject>>getConnectionAttachment().isEaglerPlayer();
	}

	@Override
	public void forEachPlayer(Consumer<IBasePlayer<PlayerObject>> callback) {
		platform.forEachPlayer((player) -> {
			callback.accept(player.getPlayerAttachment());
		});
	}

	@Override
	public void forEachEaglerPlayer(Consumer<IEaglerPlayer<PlayerObject>> callback) {
		eaglerPlayers.forEach(callback);
	}

	@Override
	public Collection<IBasePlayer<PlayerObject>> getAllPlayers() {
		return Collections2.transform(platform.getAllPlayers(),
				IPlatformPlayer<PlayerObject>::<IBasePlayer<PlayerObject>>getConnectionAttachment);
	}

	@Override
	public Set<IEaglerPlayer<PlayerObject>> getAllEaglerPlayers() {
		return ImmutableSet.copyOf(eaglerPlayers);
	}

	@Override
	public int getEaglerPlayerCount() {
		return eaglerPlayers.size();
	}

	@Override
	public Collection<byte[]> getUpdateCertificates() {
		// TODO
		return null;
	}

	@Override
	public Collection<IEaglerListenerInfo> getAllEaglerListeners() {
		return ImmutableList.copyOf(listeners.values());
	}

	@Override
	public IEaglerListenerInfo getListenerByName(String name) {
		return listeners.get(name);
	}

	@Override
	public IEaglerListenerInfo getListenerByAddress(SocketAddress address) {
		return listenersByAddress.get(address);
	}

	@Override
	public ISkinService<PlayerObject> getSkinService() {
		// TODO
		return null;
	}

	@Override
	public IVoiceService<PlayerObject> getVoiceService() {
		// TODO
		return null;
	}

	@Override
	public INotificationService<PlayerObject> getNotificationService() {
		// TODO
		return null;
	}

	@Override
	public IPauseMenuService<PlayerObject> getPauseMenuService() {
		// TODO
		return null;
	}

	@Override
	public IWebViewService<PlayerObject> getWebViewService() {
		// TODO
		return null;
	}

	@Override
	public ISupervisorService<PlayerObject> getSupervisorService() {
		// TODO
		return null;
	}

	@Override
	public IEaglerXRewindService<PlayerObject> getEaglerXRewindService() {
		return rewindService;
	}

	@Override
	public IPacketImageLoader getPacketImageLoader() {
		return PacketImageLoader.INSTANCE;
	}

	@Override
	public IServerIconLoader getServerIconLoader() {
		return ServerIconLoader.INSTANCE;
	}

	@Override
	public QueryServer getQueryServer() {
		return queryServer;
	}

	@Override
	public WebServer getWebServer() {
		return webServer;
	}

	@Override
	public IScheduler getScheduler() {
		return scheduler;
	}

	@Override
	public IBinaryHTTPClient getBinaryHTTPClient() {
		return httpClientAPI;
	}

	@Override
	public INativeZlib createNativeZlib(boolean compression, boolean decompression, int compressionLevel) {
		if(compressionLevel < 1 || compressionLevel > 9) {
			throw new IllegalArgumentException("Compression level is invalid, must be between 1 and 9");
		}else if(!compression && !decompression) {
			throw new IllegalArgumentException("Compression and decompression are both false");
		}
		return new NativeZlibWrapper(platform.createNativeZlib(compression, decompression, compressionLevel));
	}

	@Override
	public IAttributeManager getAttributeManager() {
		return attributeManager;
	}

	@Override
	public NettyUnsafe getNettyUnsafe() {
		return this;
	}

	@Override
	public ChannelFactory<? extends Channel> getChannelFactory(SocketAddress address) {
		return platform.getChannelFactory(address);
	}

	@Override
	public ChannelFactory<? extends ServerChannel> getServerChannelFactory(SocketAddress address) {
		return platform.getServerChannelFactory(address);
	}

	@Override
	public EventLoopGroup getBossEventLoopGroup() {
		return platform.getBossEventLoopGroup();
	}

	@Override
	public EventLoopGroup getWorkerEventLoopGroup() {
		return platform.getWorkerEventLoopGroup();
	}

	public IPlatformLogger logger() {
		return platform.logger();
	}

	public IEventDispatchAdapter<PlayerObject, ?> eventDispatcher() {
		return platform.eventDispatcher();
	}

	public IPlatformComponentHelper componentHelper() {
		return platform.getComponentHelper();
	}

	public IPlatformComponentBuilder componentBuilder() {
		return platform.getComponentHelper().builder();
	}

	public void setServerListConfirmCode(String code) {
		serverListConfirmCode = code;
	}

	public boolean testServerListConfirmCode(String code) {
		if(serverListConfirmCode != null) {
			if(code.equals(serverListConfirmCode)) {
				serverListConfirmCode = null;
				return true;
			}
		}
		return false;
	}

}
