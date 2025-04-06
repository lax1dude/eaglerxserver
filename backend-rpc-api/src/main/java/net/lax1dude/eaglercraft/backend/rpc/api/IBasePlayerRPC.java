package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.api.data.TexturesData;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;

public interface IBasePlayerRPC<PlayerObject> {

	IEaglerXBackendRPC<PlayerObject> getServerAPI();

	IBasePlayer<PlayerObject> getPlayer();

	boolean isEaglerPlayer();

	IEaglerPlayerRPC<PlayerObject> asEaglerPlayer();

	boolean isOpen();

	int getRPCProtocolVersion();

	int getMinecraftProtocolVersion();

	int getSupervisorNodeId();

	void addCloseListener(IRPCCloseHandler handler);

	void removeCloseListener(IRPCCloseHandler handler);

	void setBaseRequestTimeout(int seconds);

	int getBaseRequestTimeout();

	default IRPCFuture<IEaglerPlayerSkin> getPlayerSkin() {
		return getPlayerSkin(getBaseRequestTimeout());
	}

	IRPCFuture<IEaglerPlayerSkin> getPlayerSkin(int timeoutSec);

	void changePlayerSkin(IEaglerPlayerSkin skin, boolean notifyOthers);

	void changePlayerSkin(EnumPresetSkins skin, boolean notifyOthers);

	default IRPCFuture<IEaglerPlayerCape> getPlayerCape() {
		return getPlayerCape(getBaseRequestTimeout());
	}

	IRPCFuture<IEaglerPlayerCape> getPlayerCape(int timeoutSec);

	void changePlayerCape(IEaglerPlayerCape cape, boolean notifyOthers);

	void changePlayerCape(EnumPresetCapes cape, boolean notifyOthers);

	default IRPCFuture<TexturesData> getPlayerTextures() {
		return getPlayerTextures(getBaseRequestTimeout());
	}

	IRPCFuture<TexturesData> getPlayerTextures(int timeoutSec);

	void changePlayerTextures(IEaglerPlayerSkin skin, IEaglerPlayerCape cape, boolean notifyOthers);

	void changePlayerTextures(EnumPresetSkins skin, EnumPresetCapes cape, boolean notifyOthers);

	void resetPlayerSkin(boolean notifyOthers);

	void resetPlayerCape(boolean notifyOthers);

	void resetPlayerTextures(boolean notifyOthers);

	default IRPCFuture<UUID> getProfileUUID() {
		return getProfileUUID(getBaseRequestTimeout());
	}

	IRPCFuture<UUID> getProfileUUID(int timeoutSec);

	default IRPCFuture<String> getMinecraftBrand() {
		return getMinecraftBrand(getBaseRequestTimeout());
	}

	IRPCFuture<String> getMinecraftBrand(int timeoutSec);

	default IRPCFuture<UUID> getBrandUUID() {
		return getBrandUUID(getBaseRequestTimeout());
	}

	IRPCFuture<UUID> getBrandUUID(int timeoutSec);

	void sendRawCustomPayloadPacket(String channel, byte[] data);

}
