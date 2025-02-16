package net.lax1dude.eaglercraft.backend.server.base;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerImpl;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatform;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.event.IEventDispatchAdapter;
import net.lax1dude.eaglercraft.backend.server.api.EnumPlatformType;
import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeManager;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistry;
import net.lax1dude.eaglercraft.backend.server.api.internal.factory.IEaglerAPIFactory;
import net.lax1dude.eaglercraft.backend.server.api.notifications.INotificationService;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuService;
import net.lax1dude.eaglercraft.backend.server.api.query.IQueryService;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinService;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorService;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;

public class EaglerXServer<PlayerObject> implements IEaglerXServerImpl<PlayerObject>, IEaglerAPIFactory, IEaglerXServerAPI<PlayerObject> {

	private final EaglerAttributeManager attributeManager = APIFactoryImpl.INSTANCE.getEaglerAttribManager();
	private final EaglerAttributeManager.EaglerAttributeHolder attributeHolder = attributeManager.createEaglerHolder();

	private IPlatform<PlayerObject> platform;
	private Class<?> platformClazz;
	private EnumPlatformType platformType;
	private Class<PlayerObject> playerClazz;
	private IEventDispatchAdapter<PlayerObject, ?> eventDispatcher;
	private Set<EaglerPlayerInstance<PlayerObject>> eaglerPlayers;

	public EaglerXServer() {
	}

	@Override
	public void load(IPlatform.Init<PlayerObject> init) {
		eaglerPlayers = Sets.newConcurrentHashSet();
		platform = init.getPlatform();
		platformClazz = platform.getClass();
		switch(platform.getType()) {
		case BUNGEE: platformType = EnumPlatformType.BUNGEECORD; break;
		case BUKKIT: platformType = EnumPlatformType.BUKKIT; break;
		case VELOCITY: platformType = EnumPlatformType.VELOCITY; break;
		default: platformType = EnumPlatformType.STANDALONE; break;
		}
		playerClazz = platform.getPlayerClass();
		eventDispatcher = platform.eventDispatcher();
		eventDispatcher.setAPI(this);
		APIFactoryImpl.INSTANCE.initialize(playerClazz, this);
		init.setOnServerEnable(this::enableHandler);
		init.setOnServerDisable(this::disableHandler);
		init.setPipelineInitializer(new EaglerXServerNettyPipelineInitializer<PlayerObject>(this));
		init.setConnectionInitializer(new EaglerXServerConnectionInitializer<PlayerObject>(this));
		init.setPlayerInitializer(new EaglerXServerPlayerInitializer<PlayerObject>(this));
		if(platform.getType().proxy) {
			loadProxying((IPlatform.InitProxying<PlayerObject>)init);
		}else {
			loadNonProxying((IPlatform.InitNonProxying<PlayerObject>)init);
		}
	}

	private void loadProxying(IPlatform.InitProxying<PlayerObject> init) {
		
	}

	private void loadNonProxying(IPlatform.InitNonProxying<PlayerObject> init) {
		
	}

	public IPlatform<PlayerObject> getPlatform() {
		return platform;
	}

	public void enableHandler() {
		
	}

	public void disableHandler() {
		
	}

	public void registerPlayer(BasePlayerInstance<PlayerObject> playerInstance) {
		
	}

	public void registerEaglerPlayer(EaglerPlayerInstance<PlayerObject> playerInstance) {
		
	}

	public void unregisterPlayer(BasePlayerInstance<PlayerObject> playerInstance) {
		
	}

	public void unregisterEaglerPlayer(EaglerPlayerInstance<PlayerObject> playerInstance) {
		
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
	public boolean isEaglerAuthEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEaglerProtocolSupported(int vers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IBrandRegistry getBrandRegistry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayer(PlayerObject player) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(player);
		return platformPlayer != null ? platformPlayer.getPlayerAttachment() : null;
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayerByName(String playerName) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerName);
		return platformPlayer != null ? platformPlayer.getPlayerAttachment() : null;
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayerByUUID(UUID playerUUID) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerUUID);
		return platformPlayer != null ? platformPlayer.getPlayerAttachment() : null;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getEaglerPlayer(PlayerObject player) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(player);
		if(platformPlayer != null) {
			IBasePlayer<PlayerObject> basePlayer = platformPlayer.getPlayerAttachment();
			if(basePlayer.isEaglerPlayer()) {
				return (IEaglerPlayer<PlayerObject>) basePlayer;
			}
		}
		return null;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getEaglerPlayerByName(String playerName) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerName);
		if(platformPlayer != null) {
			IBasePlayer<PlayerObject> basePlayer = platformPlayer.getPlayerAttachment();
			if(basePlayer.isEaglerPlayer()) {
				return (IEaglerPlayer<PlayerObject>) basePlayer;
			}
		}
		return null;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getEaglerPlayerByUUID(UUID playerUUID) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerUUID);
		if(platformPlayer != null) {
			IBasePlayer<PlayerObject> basePlayer = platformPlayer.getPlayerAttachment();
			if(basePlayer.isEaglerPlayer()) {
				return (IEaglerPlayer<PlayerObject>) basePlayer;
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
	public Collection<IEaglerPlayer<PlayerObject>> getAllEaglerPlayers() {
		return ImmutableSet.copyOf(eaglerPlayers);
	}

	@Override
	public Collection<IEaglerListenerInfo> getAllEaglerListeners() {
		// TODO
		return null;
	}

	@Override
	public IEaglerListenerInfo getListenerByName(String name) {
		// TODO
		return null;
	}

	@Override
	public IEaglerListenerInfo getListenerByAddress(SocketAddress address) {
		// TODO
		return null;
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
	public IQueryService<PlayerObject> getQueryService() {
		// TODO
		return null;
	}

	@Override
	public ISupervisorService<PlayerObject> getSupervisorService() {
		// TODO
		return null;
	}

	@Override
	public IAttributeManager getAttributeManager() {
		return attributeManager;
	}

	public IPlatformLogger logger() {
		return platform.logger();
	}

}
