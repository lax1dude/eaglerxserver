package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;
import net.lax1dude.eaglercraft.backend.rpc.api.data.TexturesData;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;

public class BasePlayerRPCLocal<PlayerObject> implements IBasePlayerRPC<PlayerObject> {

	protected final BasePlayerLocal<PlayerObject> owner;
	protected final net.lax1dude.eaglercraft.backend.server.api.IBasePlayer<PlayerObject> delegate;
	protected int dummyTimeout = 10;
	protected int dummyTTL = 300;

	BasePlayerRPCLocal(BasePlayerLocal<PlayerObject> player,
			net.lax1dude.eaglercraft.backend.server.api.IBasePlayer<PlayerObject> delegate) {
		this.owner = player;
		this.delegate = delegate;
	}

	@Override
	public IEaglerXBackendRPC<PlayerObject> getServerAPI() {
		return owner.server;
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayer() {
		return owner;
	}

	@Override
	public boolean isOpen() {
		return owner.player.isConnected();
	}

	@Override
	public int getRPCProtocolVersion() {
		return -1;
	}

	@Override
	public int getMinecraftProtocolVersion() {
		return delegate.getMinecraftProtocol();
	}

	@Override
	public int getSupervisorNodeId() {
		return -1;
	}

	@Override
	public void setBaseRequestTimeout(int seconds) {
		dummyTimeout = seconds;
	}

	@Override
	public int getBaseRequestTimeout() {
		return dummyTimeout;
	}

	@Override
	public void setBaseCacheTTL(int seconds) {
		dummyTTL = seconds;
	}

	@Override
	public int getBaseCacheTTL() {
		return dummyTTL;
	}

	@Override
	public IRPCFuture<IEaglerPlayerSkin> getPlayerSkin(int timeoutSec, int cacheTTLSec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changePlayerSkin(IEaglerPlayerSkin skin, boolean notifyOthers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changePlayerSkin(EnumPresetSkins skin, boolean notifyOthers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IRPCFuture<IEaglerPlayerCape> getPlayerCape(int timeoutSec, int cacheTTLSec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changePlayerCape(IEaglerPlayerCape cape, boolean notifyOthers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changePlayerCape(EnumPresetCapes cape, boolean notifyOthers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IRPCFuture<TexturesData> getPlayerTextures(int timeoutSec, int cacheTTLSec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changePlayerTextures(IEaglerPlayerSkin skin, IEaglerPlayerCape cape, boolean notifyOthers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changePlayerTextures(EnumPresetSkins skin, EnumPresetCapes cape, boolean notifyOthers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetPlayerSkin(boolean notifyOthers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetPlayerCape(boolean notifyOthers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetPlayerTextures(boolean notifyOthers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IRPCFuture<UUID> getProfileUUID(int timeoutSec, int cacheTTLSec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRPCFuture<String> getMinecraftBrand(int timeoutSec, int cacheTTLSec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRPCFuture<UUID> getBrandUUID(int timeoutSec, int cacheTTLSec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendRawCustomPayloadPacket(String channel, byte[] data) {
		// TODO Auto-generated method stub
		
	}

}
