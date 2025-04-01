package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.api.data.TexturesData;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
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

	default IRPCFuture<IEaglerPlayerSkin> getPlayerSkin() {
		return getPlayerSkin(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<IEaglerPlayerSkin> getPlayerSkin(int timeoutSec) {
		return getPlayerSkin(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<IEaglerPlayerSkin> getPlayerSkin(int timeoutSec, int cacheTTLSec);

	void changePlayerSkin(IEaglerPlayerSkin skin, boolean notifyOthers);

	void changePlayerSkin(EnumPresetSkins skin, boolean notifyOthers);

	default IRPCFuture<IEaglerPlayerCape> getPlayerCape() {
		return getPlayerCape(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<IEaglerPlayerCape> getPlayerCape(int timeoutSec) {
		return getPlayerCape(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<IEaglerPlayerCape> getPlayerCape(int timeoutSec, int cacheTTLSec);

	void changePlayerCape(IEaglerPlayerCape cape, boolean notifyOthers);

	void changePlayerCape(EnumPresetCapes cape, boolean notifyOthers);

	default IRPCFuture<TexturesData> getPlayerTextures() {
		return getPlayerTextures(getBaseRequestTimeout(), getBaseCacheTTL());
	}

	default IRPCFuture<TexturesData> getPlayerTextures(int timeoutSec) {
		return getPlayerTextures(timeoutSec, getBaseCacheTTL());
	}

	IRPCFuture<TexturesData> getPlayerTextures(int timeoutSec, int cacheTTLSec);

	void changePlayerTextures(IEaglerPlayerSkin skin, IEaglerPlayerCape cape, boolean notifyOthers);

	void changePlayerTextures(EnumPresetSkins skin, EnumPresetCapes cape, boolean notifyOthers);

	void resetPlayerSkin(boolean notifyOthers);

	void resetPlayerCape(boolean notifyOthers);

	void resetPlayerTextures(boolean notifyOthers);

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
