package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.api.IBasePlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;
import net.lax1dude.eaglercraft.backend.rpc.api.data.TexturesData;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCBiConsumerFuture;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCConsumerFuture;
import net.lax1dude.eaglercraft.backend.rpc.base.RPCImmediateFuture;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;

public class BasePlayerRPCLocal<PlayerObject> implements IBasePlayerRPC<PlayerObject> {

	protected final IRPCFuture<IBasePlayerRPC<PlayerObject>> future;
	protected final BasePlayerLocal<PlayerObject> owner;
	protected final net.lax1dude.eaglercraft.backend.server.api.IBasePlayer<PlayerObject> delegate;
	protected int dummyTimeout = 10;
	protected int dummyTTL = 300;

	BasePlayerRPCLocal(BasePlayerLocal<PlayerObject> player,
			net.lax1dude.eaglercraft.backend.server.api.IBasePlayer<PlayerObject> delegate) {
		this.future = RPCImmediateFuture.create(this);
		this.owner = player;
		this.delegate = delegate;
	}

	@Override
	public IEaglerXBackendRPC<PlayerObject> getServerAPI() {
		return owner.server;
	}

	@Override
	public BasePlayerLocal<PlayerObject> getPlayer() {
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
	public IRPCFuture<IEaglerPlayerSkin> getPlayerSkin() {
		ISkinManagerBase<PlayerObject> skinMgr = delegate.getSkinManager();
		net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin skin;
		skin = skinMgr.getPlayerSkinIfLoaded();
		if(skin != null) {
			return RPCImmediateFuture.create(SkinTypesHelper.wrap(skin));
		}else {
			RPCConsumerFuture<net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin, IEaglerPlayerSkin> consumerFuture
					= new RPCConsumerFuture<net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin, IEaglerPlayerSkin>() {
				@Override
				public void accept(net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin t) {
					set(SkinTypesHelper.wrap(skin));
				}
			};
			skinMgr.resolvePlayerSkin(consumerFuture);
			return consumerFuture;
		}
	}

	@Override
	public IRPCFuture<IEaglerPlayerSkin> getPlayerSkin(int timeoutSec, int cacheTTLSec) {
		return getPlayerSkin();
	}

	@Override
	public void changePlayerSkin(IEaglerPlayerSkin skin, boolean notifyOthers) {
		delegate.getSkinManager().changePlayerSkin(SkinTypesHelper.unwrap(skin), notifyOthers);
	}

	@Override
	public void changePlayerSkin(EnumPresetSkins skin, boolean notifyOthers) {
		delegate.getSkinManager().changePlayerSkin(SkinTypesHelper.unwrap(skin), notifyOthers);
	}

	@Override
	public IRPCFuture<IEaglerPlayerCape> getPlayerCape() {
		ISkinManagerBase<PlayerObject> skinMgr = delegate.getSkinManager();
		net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape cape;
		cape = skinMgr.getPlayerCapeIfLoaded();
		if(cape != null) {
			return RPCImmediateFuture.create(SkinTypesHelper.wrap(cape));
		}else {
			RPCConsumerFuture<net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape, IEaglerPlayerCape> consumerFuture
					= new RPCConsumerFuture<net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape, IEaglerPlayerCape>() {
				@Override
				public void accept(net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape cape) {
					set(SkinTypesHelper.wrap(cape));
				}
			};
			skinMgr.resolvePlayerCape(consumerFuture);
			return consumerFuture;
		}
	}

	@Override
	public IRPCFuture<IEaglerPlayerCape> getPlayerCape(int timeoutSec, int cacheTTLSec) {
		return getPlayerCape();
	}

	@Override
	public void changePlayerCape(IEaglerPlayerCape cape, boolean notifyOthers) {
		delegate.getSkinManager().changePlayerCape(SkinTypesHelper.unwrap(cape), notifyOthers);
	}

	@Override
	public void changePlayerCape(EnumPresetCapes cape, boolean notifyOthers) {
		delegate.getSkinManager().changePlayerCape(SkinTypesHelper.unwrap(cape), notifyOthers);
	}

	@Override
	public IRPCFuture<TexturesData> getPlayerTextures() {
		ISkinManagerBase<PlayerObject> skinMgr = delegate.getSkinManager();
		net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin skin;
		net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape cape;
		skin = skinMgr.getPlayerSkinIfLoaded();
		cape = skinMgr.getPlayerCapeIfLoaded();
		if(skin != null && cape != null) {
			return RPCImmediateFuture.create(TexturesData.create(SkinTypesHelper.wrap(skin), SkinTypesHelper.wrap(cape)));
		}else {
			RPCBiConsumerFuture<net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin, net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape, TexturesData> consumerFuture
					= new RPCBiConsumerFuture<net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin, net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape, TexturesData>() {
				@Override
				public void accept(net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin skin, net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape cape) {
					set(TexturesData.create(SkinTypesHelper.wrap(skin), SkinTypesHelper.wrap(cape)));
				}
			};
			skinMgr.resolvePlayerTextures(consumerFuture);
			return consumerFuture;
		}
	}

	@Override
	public IRPCFuture<TexturesData> getPlayerTextures(int timeoutSec, int cacheTTLSec) {
		return getPlayerTextures();
	}

	@Override
	public void changePlayerTextures(IEaglerPlayerSkin skin, IEaglerPlayerCape cape, boolean notifyOthers) {
		delegate.getSkinManager().changePlayerTextures(SkinTypesHelper.unwrap(skin), SkinTypesHelper.unwrap(cape), notifyOthers);
	}

	@Override
	public void changePlayerTextures(EnumPresetSkins skin, EnumPresetCapes cape, boolean notifyOthers) {
		delegate.getSkinManager().changePlayerTextures(SkinTypesHelper.unwrap(skin), SkinTypesHelper.unwrap(cape), notifyOthers);
	}

	@Override
	public void resetPlayerSkin(boolean notifyOthers) {
		delegate.getSkinManager().resetPlayerSkin(notifyOthers);
	}

	@Override
	public void resetPlayerCape(boolean notifyOthers) {
		delegate.getSkinManager().resetPlayerCape(notifyOthers);
	}

	@Override
	public void resetPlayerTextures(boolean notifyOthers) {
		delegate.getSkinManager().resetPlayerTextures(notifyOthers);
	}

	@Override
	public IRPCFuture<UUID> getProfileUUID(int timeoutSec, int cacheTTLSec) {
		return RPCImmediateFuture.create(delegate.getUniqueId());
	}

	@Override
	public IRPCFuture<String> getMinecraftBrand(int timeoutSec, int cacheTTLSec) {
		return RPCImmediateFuture.create(delegate.getMinecraftBrand());
	}

	@Override
	public IRPCFuture<UUID> getBrandUUID(int timeoutSec, int cacheTTLSec) {
		return RPCImmediateFuture.create(delegate.getEaglerBrandUUID());
	}

	@Override
	public void sendRawCustomPayloadPacket(String channel, byte[] data) {
		owner.player.sendData(channel, data);
	}

}
