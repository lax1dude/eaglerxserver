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

import net.lax1dude.eaglercraft.backend.server.adapter.AbortLoadException;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerImpl;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatform;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.api.EnumPlatformType;
import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.backend.server.api.IServerIconLoader;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistry;
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
import net.lax1dude.eaglercraft.backend.server.base.command.CommandClientBrand;
import net.lax1dude.eaglercraft.backend.server.base.command.CommandDomain;
import net.lax1dude.eaglercraft.backend.server.base.command.CommandUserAgent;
import net.lax1dude.eaglercraft.backend.server.base.command.CommandVersion;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataListener;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataRoot;
import net.lax1dude.eaglercraft.backend.server.base.config.EaglerConfigLoader;
import net.lax1dude.eaglercraft.backend.server.base.config.SSLCertificateManager;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.PipelineTransformer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class EaglerXServer<PlayerObject> implements IEaglerXServerImpl<PlayerObject>, IEaglerAPIFactory, IEaglerXServerAPI<PlayerObject> {

	private final EaglerAttributeManager attributeManager = APIFactoryImpl.INSTANCE.getEaglerAttribManager();
	private final EaglerAttributeManager.EaglerAttributeHolder attributeHolder = attributeManager.createEaglerHolder();

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
	private PipelineTransformer pipelineTransformer;
	private SSLCertificateManager certificateManager;

	public EaglerXServer() {
	}

	@Override
	public void load(IPlatform.Init<PlayerObject> init) {
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
		brandRegistry = new BrandRegistry();
		queryServer = new QueryServer(this);
		webServer = new WebServer(this);
		pipelineTransformer = new PipelineTransformer(this);
		certificateManager = new SSLCertificateManager(logger());
		
		try {
			config = EaglerConfigLoader.loadConfig(platform);
		} catch (IOException e) {
			throw new AbortLoadException("Could not read one or more config files!", e);
		}
		
		logger().info("Server Name: \"" + config.getSettings().getServerName() + "\"");
		
		init.setOnServerEnable(this::enableHandler);
		init.setOnServerDisable(this::disableHandler);
		init.setPipelineInitializer(new EaglerXServerNettyPipelineInitializer<PlayerObject>(this));
		init.setConnectionInitializer(new EaglerXServerConnectionInitializer<PlayerObject>(this));
		init.setPlayerInitializer(new EaglerXServerPlayerInitializer<PlayerObject>(this));
		init.setCommandRegistry(Arrays.asList(
				new CommandVersion<PlayerObject>(this),
				new CommandClientBrand<PlayerObject>(this),
				new CommandDomain<PlayerObject>(this),
				new CommandUserAgent<PlayerObject>(this)
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
		eventDispatcher.dispatchInitializePlayerEvent(playerInstance, null);
	}

	public void unregisterPlayer(BasePlayerInstance<PlayerObject> playerInstance) {
		
	}

	public void unregisterEaglerPlayer(EaglerPlayerInstance<PlayerObject> playerInstance) {
		if(!eaglerPlayers.remove(playerInstance)) return;
		
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

	@Override
	public boolean isAuthenticationEventsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEaglerProtocolSupported(int vers) {
		return GamePluginMessageProtocol.getByVersion(vers) != null;
	}

	@Override
	public IBrandRegistry getBrandRegistry() {
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
		// TODO
		return null;
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
	public IQueryServer getQueryServer() {
		return queryServer;
	}

	@Override
	public IWebServer getWebServer() {
		return webServer;
	}

	@Override
	public IAttributeManager getAttributeManager() {
		return attributeManager;
	}

	public IPlatformLogger logger() {
		return platform.logger();
	}

}
