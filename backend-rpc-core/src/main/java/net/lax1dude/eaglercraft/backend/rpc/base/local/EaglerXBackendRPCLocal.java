package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatform.Init;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatform.InitLocalMode;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.ISkinImageLoader;
import net.lax1dude.eaglercraft.backend.rpc.base.EaglerXBackendRPCBase;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewChannelEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebViewMessageEvent;

public class EaglerXBackendRPCLocal<PlayerObject> extends EaglerXBackendRPCBase<PlayerObject> {

	private final ConcurrentMap<PlayerObject, BasePlayerLocal<PlayerObject>> basePlayerMap = (new MapMaker())
			.initialCapacity(256).concurrencyLevel(16).makeMap();

	private final ConcurrentMap<PlayerObject, EaglerPlayerLocal<PlayerObject>> eaglerPlayerMap = (new MapMaker())
			.initialCapacity(256).concurrencyLevel(16).makeMap();

	private IEaglerXServerAPI<PlayerObject> serverAPI;
	private ISkinImageLoader skinLoaderCache;
	private ISkinImageLoader skinLoaderNoCache;
	private IPacketImageLoader packetImageLoader;
	private VoiceServiceLocal<PlayerObject> voiceService;

	@Override
	protected void load0(Init<PlayerObject> platf) {
		platf.setOnServerEnable(this::enableHandler);
		platf.setOnServerDisable(this::disableHandler);
		platf.setPlayerInitializer(new BackendRPCPlayerInitializer<>(this));
		InitLocalMode<PlayerObject> platfLocal = platf.localMode();
		platfLocal.setOnWebViewChannel(this::fireWebViewChannel);
		platfLocal.setOnWebViewMessage(this::fireWebViewMessage);
		platfLocal.setOnVoiceChange(this::fireVoiceChange);
		serverAPI = IEaglerXServerAPI.instance(playerClass);
		skinLoaderCache = new SkinTypesHelper(serverAPI.getSkinService().getSkinLoader(true));
		skinLoaderNoCache = new SkinTypesHelper(serverAPI.getSkinService().getSkinLoader(false));
		packetImageLoader = new PacketImageDataHelper(serverAPI.getPacketImageLoader());
		voiceService = new VoiceServiceLocal<>(this, serverAPI.getVoiceService());
	}

	private void enableHandler() {
		
	}

	private void disableHandler() {
		
	}

	private void fireWebViewChannel(IEaglercraftWebViewChannelEvent<PlayerObject> event) {
		EaglerPlayerLocal<PlayerObject> player = eaglerPlayerMap.get(event.getPlayer().getPlayerObject());
		if(player != null) {
			player.playerRPC().fireLocalWebViewChannel(event);
		}
	}

	private void fireWebViewMessage(IEaglercraftWebViewMessageEvent<PlayerObject> event) {
		EaglerPlayerLocal<PlayerObject> player = eaglerPlayerMap.get(event.getPlayer().getPlayerObject());
		if(player != null) {
			player.playerRPC().fireLocalWebViewMessage(event);
		}
	}

	private void fireVoiceChange(IEaglercraftVoiceChangeEvent<PlayerObject> event) {
		EaglerPlayerLocal<PlayerObject> player = eaglerPlayerMap.get(event.getPlayer().getPlayerObject());
		if(player != null) {
			player.playerRPC().fireLocalVoiceChange(event);
		}
	}

	void registerEaglerPlayer(EaglerPlayerLocal<PlayerObject> player) {
		PlayerObject playerObj = player.getPlayerObject();
		if(basePlayerMap.putIfAbsent(playerObj, player) != null) {
			throw new IllegalStateException("Player is already registered!");
		}
		eaglerPlayerMap.put(playerObj, player);
	}

	void confirmEaglerPlayer(EaglerPlayerLocal<PlayerObject> player) {
		if(eaglerPlayerMap.get(player.getPlayerObject()) != null) {
			platform.eventDispatcher().dispatchPlayerReadyEvent(player);
			net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer<PlayerObject> eagPlayer =
					((EaglerPlayerRPCLocal<PlayerObject>) player.playerRPC).delegate;
			if(eagPlayer.isVoiceCapable()) {
				net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager<PlayerObject> voiceManager =
						eagPlayer.getVoiceManager();
				net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel channel =
						voiceManager.getVoiceChannel();
				net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel channel2 =
						VoiceChannelHelper.unwrap(platform.eventDispatcher().dispatchVoiceCapableEvent(player,
								VoiceChannelHelper.wrap(channel)).getTargetChannel());
				if(channel != channel2) {
					voiceManager.setVoiceChannel(channel2);
				}
			}
		}
	}

	void unregisterEaglerPlayer(EaglerPlayerLocal<PlayerObject> player) {
		PlayerObject playerObj = player.getPlayerObject();
		if(basePlayerMap.remove(playerObj) != null) {
			eaglerPlayerMap.remove(playerObj);
			player.handleDestroyed();
		}
	}

	void registerVanillaPlayer(BasePlayerLocal<PlayerObject> player) {
		if(basePlayerMap.put(player.getPlayerObject(), player) != null) {
			throw new IllegalStateException("Player is already registered!");
		}
	}

	void confirmVanillaPlayer(BasePlayerLocal<PlayerObject> player) {
	}

	void unregisterVanillaPlayer(BasePlayerLocal<PlayerObject> player) {
		if(basePlayerMap.remove(player.getPlayerObject()) != null) {
			player.handleDestroyed();
		}
	}

	IEaglerXServerAPI<PlayerObject> serverAPI() {
		return serverAPI;
	}

	@Override
	public VoiceServiceLocal<PlayerObject> getVoiceService() {
		return voiceService;
	}

	@Override
	public ISkinImageLoader getSkinImageLoader(boolean cacheEnabled) {
		return cacheEnabled ? skinLoaderCache : skinLoaderNoCache;
	}

	@Override
	public IPacketImageLoader getPacketImageLoader() {
		return packetImageLoader;
	}

	@Override
	public Set<Class<?>> getComponentTypes() {
		return serverAPI.getComponentTypes();
	}

	@Override
	public IPauseMenuBuilder createPauseMenuBuilder() {
		return new PauseMenuBuilderLocal(serverAPI.getPauseMenuService().createPauseMenuBuilder());
	}

	@Override
	public <ComponentType> INotificationBuilder<ComponentType> createNotificationBadgeBuilder(
			Class<ComponentType> componentType) {
		return new NotificationBuilderLocal<ComponentType>(
				serverAPI.getNotificationService().createNotificationBuilder(componentType));
	}

	@Override
	public IBasePlayer<PlayerObject> getBasePlayer(PlayerObject player) {
		return basePlayerMap.get(player);
	}

	@Override
	public IBasePlayer<PlayerObject> getBasePlayerByName(String playerName) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerName);
		if(platformPlayer != null) {
			return platformPlayer.getAttachment();
		}
		return null;
	}

	@Override
	public IBasePlayer<PlayerObject> getBasePlayerByUUID(UUID playerUUID) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerUUID);
		if(platformPlayer != null) {
			return platformPlayer.getAttachment();
		}
		return null;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getEaglerPlayer(PlayerObject player) {
		return eaglerPlayerMap.get(player);
	}

	@Override
	public IEaglerPlayer<PlayerObject> getEaglerPlayerByName(String playerName) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerName);
		if(platformPlayer != null) {
			BasePlayerLocal<PlayerObject> basePlayer = platformPlayer.getAttachment();
			if(basePlayer != null) {
				return basePlayer.asEaglerPlayer();
			}
		}
		return null;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getEaglerPlayerByUUID(UUID playerUUID) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerUUID);
		if(platformPlayer != null) {
			BasePlayerLocal<PlayerObject> basePlayer = platformPlayer.getAttachment();
			if(basePlayer != null) {
				return basePlayer.asEaglerPlayer();
			}
		}
		return null;
	}

	@Override
	public boolean isEaglerPlayer(PlayerObject player) {
		return eaglerPlayerMap.containsKey(player);
	}

	@Override
	public boolean isEaglerPlayerByName(String playerName) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerName);
		if(platformPlayer != null) {
			BasePlayerLocal<PlayerObject> basePlayer = platformPlayer.getAttachment();
			if(basePlayer != null) {
				return basePlayer.isEaglerPlayer();
			}
		}
		return false;
	}

	@Override
	public boolean isEaglerPlayerByUUID(UUID playerUUID) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerUUID);
		if(platformPlayer != null) {
			BasePlayerLocal<PlayerObject> basePlayer = platformPlayer.getAttachment();
			if(basePlayer != null) {
				return basePlayer.isEaglerPlayer();
			}
		}
		return false;
	}

	@Override
	public Collection<IBasePlayer<PlayerObject>> getAllPlayers() {
		return ImmutableList.copyOf(basePlayerMap.values());
	}

	@Override
	public Collection<IEaglerPlayer<PlayerObject>> getAllEaglerPlayers() {
		return ImmutableList.copyOf(eaglerPlayerMap.values());
	}

	@Override
	public UUID intern(UUID uuid) {
		return serverAPI.intern(uuid);
	}

	@Override
	public boolean isLocal() {
		return true;
	}

}
