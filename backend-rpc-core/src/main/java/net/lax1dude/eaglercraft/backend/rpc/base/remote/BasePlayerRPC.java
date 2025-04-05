package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCCloseHandler;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;
import net.lax1dude.eaglercraft.backend.rpc.api.data.TexturesData;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;

public class BasePlayerRPC<PlayerObject> implements IBasePlayerRPC<PlayerObject> {

	protected int baseRequestTimeout = 10;
	protected int baseCacheTTL = 300;

	@Override
	public IEaglerXBackendRPC<PlayerObject> getServerAPI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBasePlayer<PlayerObject> getPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEaglerPlayer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IEaglerPlayerRPC<PlayerObject> asEaglerPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getRPCProtocolVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinecraftProtocolVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSupervisorNodeId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addCloseListener(IRPCCloseHandler handler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeCloseListener(IRPCCloseHandler handler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBaseRequestTimeout(int seconds) {
		baseRequestTimeout = seconds;
	}

	@Override
	public int getBaseRequestTimeout() {
		return baseRequestTimeout;
	}

	@Override
	public void setBaseCacheTTL(int seconds) {
		baseCacheTTL = seconds;
	}

	@Override
	public int getBaseCacheTTL() {
		return baseCacheTTL;
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
