package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableList;
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
import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.voice.protocol.EaglerVCProtocol;

public class EaglerXBackendRPCRemote<PlayerObject> extends EaglerXBackendRPCBase<PlayerObject> {

	private final ConcurrentMap<PlayerObject, PlayerInstanceRemote<PlayerObject>> basePlayerMap = (new MapMaker())
			.initialCapacity(256).concurrencyLevel(16).makeMap();

	private final ConcurrentMap<PlayerObject, PlayerInstanceRemote<PlayerObject>> eaglerPlayerMap = (new MapMaker())
			.initialCapacity(256).concurrencyLevel(16).makeMap();

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
		if(basePlayerMap.remove(player) != null && playerInstance.isEaglerPlayer()) {
			eaglerPlayerMap.remove(player);
		}
	}

	private void handleRPCMessage(IBackendRPCMessageChannel<PlayerObject> channel,
			IPlatformPlayer<PlayerObject> player, byte[] contents) {

	}

	private void handleReadyMessage(IBackendRPCMessageChannel<PlayerObject> channel,
			IPlatformPlayer<PlayerObject> player, byte[] contents) {

	}

	private void handleVoiceMessage(IBackendRPCMessageChannel<PlayerObject> channel,
			IPlatformPlayer<PlayerObject> player, byte[] contents) {

	}

	@Override
	public IVoiceServiceX<PlayerObject> getVoiceService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISkinImageLoader getSkinImageLoader(boolean enableCache) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPacketImageLoader getPacketImageLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Class<?>> getComponentTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPauseMenuBuilder createPauseMenuBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <ComponentType> INotificationBuilder<ComponentType> createNotificationBadgeBuilder(
			Class<ComponentType> componentType) {
		// TODO Auto-generated method stub
		return null;
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
	public boolean isLocal() {
		return false;
	}

}
