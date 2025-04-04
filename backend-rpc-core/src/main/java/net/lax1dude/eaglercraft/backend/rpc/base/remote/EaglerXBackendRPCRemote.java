package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableList;

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

	private void handleRPCMessage(IBackendRPCMessageChannel<PlayerObject> channel, IPlatformPlayer<PlayerObject> player, byte[] contents) {
		
	}

	private void handleReadyMessage(IBackendRPCMessageChannel<PlayerObject> channel, IPlatformPlayer<PlayerObject> player, byte[] contents) {
		
	}

	private void handleVoiceMessage(IBackendRPCMessageChannel<PlayerObject> channel, IPlatformPlayer<PlayerObject> player, byte[] contents) {
		
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBasePlayer<PlayerObject> getBasePlayerByName(String playerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBasePlayer<PlayerObject> getBasePlayerByUUID(UUID playerUUID) {
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
	public Collection<IBasePlayer<PlayerObject>> getAllPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IEaglerPlayer<PlayerObject>> getAllEaglerPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

}
