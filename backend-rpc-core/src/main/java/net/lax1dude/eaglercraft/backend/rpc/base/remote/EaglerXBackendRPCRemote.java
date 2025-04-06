package net.lax1dude.eaglercraft.backend.rpc.base.remote;

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
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceServiceX;
import net.lax1dude.eaglercraft.backend.rpc.base.EaglerXBackendRPCBase;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.message.BackendRPCMessageChannel;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.SkinImageLoaderImpl;
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

	@Override
	protected void load0(Init<PlayerObject> platf) {
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
		componentClass = platform.getComponentHelper().getComponentType();
		componentTypes = Collections.singleton(componentClass);
		channelRPCName = platform.isPost_v1_13() ? EaglerBackendRPCProtocol.CHANNEL_NAME_MODERN
				: EaglerBackendRPCProtocol.CHANNEL_NAME;
		channelVoiceName = platform.isPost_v1_13() ? EaglerVCProtocol.CHANNEL_NAME_MODERN
				: EaglerVCProtocol.CHANNEL_NAME;
	}

	private void enableHandler() {
		
	}

	private void disableHandler() {
		
	}

	void registerPlayer(PlayerInstanceRemote<PlayerObject> playerInstance) {
		
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
		PlayerInstanceRemote<PlayerObject> playerInstance = player.getAttachment();
		if(playerInstance != null) {
			playerInstance.handleRPCMessage(contents);
		}
	}

	private void handleReadyMessage(IBackendRPCMessageChannel<PlayerObject> channel,
			IPlatformPlayer<PlayerObject> player, byte[] contents) {
		if(contents.length > 0) {
			PlayerInstanceRemote<PlayerObject> playerInstance = player.getAttachment();
			if(playerInstance != null) {
				playerInstance.handleReadyMessage(contents[0] != (byte)0);
			}
		}else {
			logger().error("Zero-length ready plugin message recieved, you are most likely "
					+ "still running the old EaglerXBungee/EaglerXVelocity plugin instead of "
					+ "EaglerXServer on your proxy");
		}
	}

	private void handleVoiceMessage(IBackendRPCMessageChannel<PlayerObject> channel,
			IPlatformPlayer<PlayerObject> player, byte[] contents) {
		PlayerInstanceRemote<PlayerObject> playerInstance = player.getAttachment();
		if(playerInstance != null) {
			playerInstance.handleVoiceMessage(contents);
		}
	}

	@Override
	public IVoiceServiceX<PlayerObject> getVoiceService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISkinImageLoader getSkinImageLoader(boolean enableCache) {
		return SkinImageLoaderImpl.getSkinLoader(enableCache);
	}

	@Override
	public IPacketImageLoader getPacketImageLoader() {
		// TODO Auto-generated method stub
		return null;
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
			PlayerInstanceRemote<PlayerObject> basePlayer = platformPlayer.getAttachment();
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
			PlayerInstanceRemote<PlayerObject> basePlayer = platformPlayer.getAttachment();
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
			PlayerInstanceRemote<PlayerObject> basePlayer = platformPlayer.getAttachment();
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
			PlayerInstanceRemote<PlayerObject> basePlayer = platformPlayer.getAttachment();
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
