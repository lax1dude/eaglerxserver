package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatform.Init;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.backend.rpc.api.notifications.INotificationBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.ISkinImageLoader;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceServiceX;
import net.lax1dude.eaglercraft.backend.rpc.base.EaglerXBackendRPCBase;

public class EaglerXBackendRPCRemote<PlayerObject> extends EaglerXBackendRPCBase<PlayerObject> {

	@Override
	protected void load0(Init<PlayerObject> platf) {
		platf.setOnServerEnable(this::enableHandler);
		platf.setOnServerDisable(this::disableHandler);
		
	}

	private void enableHandler() {
		
	}

	private void disableHandler() {
		
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
