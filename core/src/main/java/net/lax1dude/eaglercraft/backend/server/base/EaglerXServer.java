package net.lax1dude.eaglercraft.backend.server.base;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerImpl;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerListener;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatform;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnectionInitializer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayerInitializer;
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

	public EaglerXServer() {
	}

	@Override
	public void load(IPlatform.Init<PlayerObject> init) {
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
		APIFactoryImpl.INSTANCE.initialize(playerClazz, this);
		init.setOnServerEnable(this::enableHandler);
		init.setOnServerDisable(this::disableHandler);
		init.setPipelineInitializer(this::initializePipelineHandler);
		init.setPlayerInitializer(this::initializePlayerHandler);
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

	public void initializePipelineHandler(IEaglerXServerListener listener, IPlatformConnection conn,
			IPlatformConnectionInitializer<Object> initializer) {

	}

	public void initializePlayerHandler(IPlatformPlayer player, IPlatformPlayerInitializer<Object> initializer) {

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayerByName(String playerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayerByUUID(UUID playerUUID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getEaglerPlayer(PlayerObject player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getEaglerPlayerByName(String playerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getEaglerPlayerByUUID(UUID playerUUID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEaglerPlayer(PlayerObject player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEaglerPlayerByName(String playerName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEaglerPlayerByUUID(UUID playerUUID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getAllEaglerPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IEaglerListenerInfo> getAllEaglerListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEaglerListenerInfo getListenerByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEaglerListenerInfo getListenerByAddress(SocketAddress address) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISkinService<PlayerObject> getSkinService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVoiceService<PlayerObject> getVoiceService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INotificationService<PlayerObject> getNotificationService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPauseMenuService<PlayerObject> getPauseMenuService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWebViewService<PlayerObject> getWebViewService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQueryService<PlayerObject> getQueryService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISupervisorService<PlayerObject> getSupervisorService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAttributeManager getAttributeManager() {
		return attributeManager;
	}

}
