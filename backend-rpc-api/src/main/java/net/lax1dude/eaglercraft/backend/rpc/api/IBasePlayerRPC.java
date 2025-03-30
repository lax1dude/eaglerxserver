package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;

public interface IBasePlayerRPC<PlayerObject> {

	IEaglerXServerRPC<PlayerObject> getServerAPI();

	IBasePlayer<PlayerObject> getPlayer();

	boolean isOpen();

	int getRPCProtocolVersion();

	int getMinecraftProtocolVersion();

	int getSupervisorNodeId();

	void setBaseRequestTimeout(int seconds);

	int getBaseRequestTimeout();

	void setBaseCacheTTL(int seconds);

	int getBaseCacheTTL();

	default IRPCFuture<IEaglerPlayerSkin> getEaglerSkin() {
		return getEaglerSkin(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<IEaglerPlayerSkin> getEaglerSkin(int timeoutSec) {
		return getEaglerSkin(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<IEaglerPlayerSkin> getEaglerSkin(int timeoutSec, int cacheTTLSec);

	void changeEaglerSkin(IEaglerPlayerSkin skin, boolean notifyOthers);

	default IRPCFuture<IEaglerPlayerSkin> getEaglerCape() {
		return getEaglerCape(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<IEaglerPlayerSkin> getEaglerCape(int timeoutSec) {
		return getEaglerCape(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<IEaglerPlayerSkin> getEaglerCape(int timeoutSec, int cacheTTLSec);

	void changePlayerCape(IEaglerPlayerCape cape, boolean notifyOthers);

	void resetForcedSkin(boolean notifyOtherPlayers);

	void resetForcedCape(boolean notifyOtherPlayers);

	void resetForcedMulti(boolean resetSkin, boolean resetCape, boolean notifyOtherPlayers);

	default IRPCFuture<UUID> getProfileUUID() {
		return getProfileUUID(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<UUID> getProfileUUID(int timeoutSec) {
		return getProfileUUID(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<UUID> getProfileUUID(int timeoutSec, int cacheTTLSec);

	default IRPCFuture<String> getMinecraftBrand() {
		return getMinecraftBrand(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<String> getMinecraftBrand(int timeoutSec) {
		return getMinecraftBrand(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<String> getMinecraftBrand(int timeoutSec, int cacheTTLSec);

	default IRPCFuture<UUID> getBrandUUID() {
		return getBrandUUID(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<UUID> getBrandUUID(int timeoutSec) {
		return getBrandUUID(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<UUID> getBrandUUID(int timeoutSec, int cacheTTLSec);

	void sendRawCustomPayloadPacket(String channel, byte[] data);

}
