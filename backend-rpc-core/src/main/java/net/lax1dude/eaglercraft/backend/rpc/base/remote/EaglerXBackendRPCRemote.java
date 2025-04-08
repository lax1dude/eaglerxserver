package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.MapMaker;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IBackendRPCMessageChannel;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatform.Init;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatform.InitRemoteMode;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.ISkinImageLoader;
import net.lax1dude.eaglercraft.backend.rpc.base.EaglerXBackendRPCBase;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.config.BackendRPCConfigLoader;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.config.ConfigDataRoot;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.config.ConfigDataSettings;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.message.BackendRPCMessageChannel;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.SkinImageLoaderImpl;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.voice.IVoiceServiceImpl;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.voice.VoiceServiceDisabled;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.voice.VoiceServiceRemote;
import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.voice.protocol.EaglerVCProtocol;

public class EaglerXBackendRPCRemote<PlayerObject> extends EaglerXBackendRPCBase<PlayerObject> {

	public static final Interner<UUID> uuidInterner = Interners.newWeakInterner();

	private final ConcurrentMap<PlayerObject, PlayerInstanceRemote<PlayerObject>> basePlayerMap = (new MapMaker())
			.initialCapacity(256).concurrencyLevel(16).makeMap();

	private final ConcurrentMap<PlayerObject, PlayerInstanceRemote<PlayerObject>> eaglerPlayerMap = (new MapMaker())
			.initialCapacity(256).concurrencyLevel(16).makeMap();

	private Class<?> componentClass;
	private Set<Class<?>> componentTypes;
	private String channelRPCName;
	private String channelVoiceName;
	private ConfigDataRoot config;
	private IVoiceServiceImpl<PlayerObject> voiceService;

	@Override
	protected void load0(Init<PlayerObject> platf) {
		componentClass = platform.getComponentHelper().getComponentType();
		componentTypes = Collections.singleton(componentClass);
		
		try {
			config = BackendRPCConfigLoader.loadConfig(platform.getDataFolder());
		}catch(IOException ex) {
			throw new IllegalStateException("Failed to read EaglerXBackendRPC config file!", ex);
		}
		
		if(config.getConfigSettings().isForceModernizedChannelNames()) {
			platform.setForcePost_v1_13(true);
		}
		
		platf.setOnServerEnable(this::enableHandler);
		platf.setOnServerDisable(this::disableHandler);
		InitRemoteMode<PlayerObject> platfRemote = platf.remoteMode();
		platfRemote.setEaglerPlayerChannels(ImmutableList.of(
				new BackendRPCMessageChannel<PlayerObject>(EaglerBackendRPCProtocol.CHANNEL_NAME,
						EaglerBackendRPCProtocol.CHANNEL_NAME_MODERN, this::handleRPCMessage),
				new BackendRPCMessageChannel<PlayerObject>(EaglerBackendRPCProtocol.CHANNEL_NAME_READY,
						EaglerBackendRPCProtocol.CHANNEL_NAME_READY_MODERN, this::handleReadyMessage),
				new BackendRPCMessageChannel<PlayerObject>(EaglerVCProtocol.CHANNEL_NAME,
						EaglerVCProtocol.CHANNEL_NAME_MODERN, this::handleVoiceMessage)));
		
		if(platform.isPost_v1_13()) {
			logger().info("Using modernized 1.13+ channel names for RPC, make sure EaglerXServer is configured to use modernized channel names!");
		}
		
		ConfigDataSettings.ConfigDataBackendRPC confBackendRPC = config.getConfigSettings().getConfigBackendRPC();
		setBaseRequestTimeout(confBackendRPC.getBaseRequestTimeoutSec());
		createTimeoutLoop((long)(1000000000.0 * confBackendRPC.getTimeoutResolutionSec()));
		
		channelRPCName = platform.isPost_v1_13() ? EaglerBackendRPCProtocol.CHANNEL_NAME_MODERN
				: EaglerBackendRPCProtocol.CHANNEL_NAME;
		channelVoiceName = platform.isPost_v1_13() ? EaglerVCProtocol.CHANNEL_NAME_MODERN
				: EaglerVCProtocol.CHANNEL_NAME;
		
		if(config.getConfigSettings().getConfigBackendVoice().isEnableBackendVoiceService()) {
			voiceService = new VoiceServiceRemote<>(this);
			voiceService.setICEServers(config.getConfigICEServers().getICEServers());
			voiceService.setOverrideICEServers(config.getConfigICEServers().isReplaceICEServerList());
		}else {
			voiceService = new VoiceServiceDisabled<>(this);
		}
	}

	private void enableHandler() {
		
	}

	private void disableHandler() {
		cancelTimeoutLoop();
	}

	void registerPlayer(PlayerInstanceRemote<PlayerObject> playerInstance) {
		PlayerObject playerObj = playerInstance.getPlayerObject();
		if(basePlayerMap.putIfAbsent(playerObj, playerInstance) != null) {
			throw new IllegalStateException("Player is already registered!");
		}
		if(playerInstance.isEaglerPlayer()) {
			eaglerPlayerMap.put(playerObj, playerInstance);
		}
	}

	void registerPlayerEagler(PlayerInstanceRemote<PlayerObject> playerInstance) {
		PlayerObject playerObj = playerInstance.getPlayerObject();
		if(basePlayerMap.containsKey(playerObj)) {
			eaglerPlayerMap.put(playerObj, playerInstance);
		}
	}

	void confirmPlayer(PlayerInstanceRemote<PlayerObject> playerInstance) {
	}

	void unregisterPlayer(PlayerInstanceRemote<PlayerObject> playerInstance) {
		PlayerObject player = playerInstance.getPlayerObject();
		if(basePlayerMap.remove(player) != null) {
			if(playerInstance.isEaglerPlayer()) {
				eaglerPlayerMap.remove(player);
			}
			playerInstance.handleDestroyed();
		}
	}

	private void handleRPCMessage(IBackendRPCMessageChannel<PlayerObject> channel,
			IPlatformPlayer<PlayerObject> player, byte[] contents) {
		player.<PlayerInstanceRemote<PlayerObject>>getAttachment().handleRPCMessage(contents);
	}

	private void handleReadyMessage(IBackendRPCMessageChannel<PlayerObject> channel,
			IPlatformPlayer<PlayerObject> player, byte[] contents) {
		if(contents.length > 0) {
			player.<PlayerInstanceRemote<PlayerObject>>getAttachment().handleReadyMessage(contents[0] != (byte)0);
		}else {
			logger().error("Zero-length ready plugin message recieved, you are most likely "
					+ "still running the old EaglerXBungee/EaglerXVelocity plugin instead of "
					+ "EaglerXServer on your proxy");
		}
	}

	private void handleVoiceMessage(IBackendRPCMessageChannel<PlayerObject> channel,
			IPlatformPlayer<PlayerObject> player, byte[] contents) {
		player.<PlayerInstanceRemote<PlayerObject>>getAttachment().handleVoiceMessage(contents);
	}

	@Override
	public IVoiceServiceImpl<PlayerObject> getVoiceService() {
		return voiceService;
	}

	@Override
	public ISkinImageLoader getSkinImageLoader(boolean enableCache) {
		return SkinImageLoaderImpl.getSkinLoader(enableCache);
	}

	@Override
	public IPacketImageLoader getPacketImageLoader() {
		return PacketImageLoader.INSTANCE;
	}

	@Override
	public Set<Class<?>> getComponentTypes() {
		return componentTypes;
	}

	@Override
	public IPauseMenuBuilder createPauseMenuBuilder() {
		return new PauseMenuBuilder();
	}

	@Override
	public <ComponentType> INotificationBuilder<ComponentType> createNotificationBadgeBuilder(
			Class<ComponentType> componentType) {
		if(componentType != this.componentClass) {
			throw new ClassCastException("Component class " + componentType.getName() + " is not supported on this platform!");
		}
		return new NotificationBuilder<ComponentType>(platform.getComponentHelper());
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
			return platformPlayer.<PlayerInstanceRemote<PlayerObject>>getAttachment().asEaglerPlayer();
		}
		return null;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getEaglerPlayerByUUID(UUID playerUUID) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerUUID);
		if(platformPlayer != null) {
			return platformPlayer.<PlayerInstanceRemote<PlayerObject>>getAttachment().asEaglerPlayer();
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
			return platformPlayer.<PlayerInstanceRemote<PlayerObject>>getAttachment().isEaglerPlayer();
		}
		return false;
	}

	@Override
	public boolean isEaglerPlayerByUUID(UUID playerUUID) {
		IPlatformPlayer<PlayerObject> platformPlayer = platform.getPlayer(playerUUID);
		if(platformPlayer != null) {
			return platformPlayer.<PlayerInstanceRemote<PlayerObject>>getAttachment().isEaglerPlayer();
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
		return uuidInterner.intern(uuid);
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	public String getChannelRPCName() {
		return channelRPCName;
	}

	public String getChannelVoiceName() {
		return channelVoiceName;
	}

}
