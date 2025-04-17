package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.rpc.api.data.TexturesData;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;

public interface IBasePlayerRPC<PlayerObject> {

	@Nonnull
	IEaglerXBackendRPC<PlayerObject> getServerAPI();

	@Nonnull
	IBasePlayer<PlayerObject> getPlayer();

	boolean isEaglerPlayer();

	@Nullable
	IEaglerPlayerRPC<PlayerObject> asEaglerPlayer();

	boolean isOpen();

	int getRPCProtocolVersion();

	int getMinecraftProtocolVersion();

	int getSupervisorNodeId();

	void addCloseListener(@Nonnull IRPCCloseHandler handler);

	void removeCloseListener(@Nonnull IRPCCloseHandler handler);

	void setBaseRequestTimeout(int seconds);

	int getBaseRequestTimeout();

	@Nonnull
	default IRPCFuture<IEaglerPlayerSkin> getPlayerSkin() {
		return getPlayerSkin(getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<IEaglerPlayerSkin> getPlayerSkin(int timeoutSec);

	default void changePlayerSkin(@Nonnull IEaglerPlayerSkin skin) {
		changePlayerSkin(skin, true);
	}

	void changePlayerSkin(@Nonnull IEaglerPlayerSkin skin, boolean notifyOthers);

	default void changePlayerSkin(@Nonnull EnumPresetSkins skin) {
		changePlayerSkin(skin, true);
	}

	void changePlayerSkin(@Nonnull EnumPresetSkins skin, boolean notifyOthers);

	@Nonnull
	default IRPCFuture<IEaglerPlayerCape> getPlayerCape() {
		return getPlayerCape(getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<IEaglerPlayerCape> getPlayerCape(int timeoutSec);

	default void changePlayerCape(@Nonnull IEaglerPlayerCape cape) {
		changePlayerCape(cape, true);
	}

	void changePlayerCape(@Nonnull IEaglerPlayerCape cape, boolean notifyOthers);

	default void changePlayerCape(@Nonnull EnumPresetCapes cape) {
		changePlayerCape(cape, true);
	}

	void changePlayerCape(@Nonnull EnumPresetCapes cape, boolean notifyOthers);

	@Nonnull
	default IRPCFuture<TexturesData> getPlayerTextures() {
		return getPlayerTextures(getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<TexturesData> getPlayerTextures(int timeoutSec);

	default void changePlayerTextures(@Nonnull IEaglerPlayerSkin skin, @Nonnull IEaglerPlayerCape cape) {
		changePlayerTextures(skin, cape, true);
	}

	void changePlayerTextures(@Nonnull IEaglerPlayerSkin skin, @Nonnull IEaglerPlayerCape cape, boolean notifyOthers);

	default void changePlayerTextures(@Nonnull EnumPresetSkins skin, @Nonnull EnumPresetCapes cape) {
		changePlayerTextures(skin, cape, true);
	}

	void changePlayerTextures(@Nonnull EnumPresetSkins skin, @Nonnull EnumPresetCapes cape, boolean notifyOthers);

	default void resetPlayerSkin() {
		resetPlayerSkin(true);
	}

	void resetPlayerSkin(boolean notifyOthers);

	default void resetPlayerCape() {
		resetPlayerCape(true);
	}

	void resetPlayerCape(boolean notifyOthers);

	default void resetPlayerTextures() {
		resetPlayerTextures(true);
	}

	void resetPlayerTextures(boolean notifyOthers);

	@Nonnull
	default IRPCFuture<UUID> getProfileUUID() {
		return getProfileUUID(getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<UUID> getProfileUUID(int timeoutSec);

	@Nonnull
	default IRPCFuture<String> getMinecraftBrand() {
		return getMinecraftBrand(getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<String> getMinecraftBrand(int timeoutSec);

	@Nonnull
	default IRPCFuture<UUID> getBrandUUID() {
		return getBrandUUID(getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<UUID> getBrandUUID(int timeoutSec);

	@Nonnull
	default IRPCFuture<IEaglerPlayerSkin> getSkinByURL(@Nonnull String url) {
		return getSkinByURL(url, getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<IEaglerPlayerSkin> getSkinByURL(@Nonnull String url, int timeoutSec);

	@Nonnull
	default IRPCFuture<IEaglerPlayerCape> getCapeByURL(@Nonnull String url) {
		return getCapeByURL(url, getBaseRequestTimeout());
	}

	@Nonnull
	IRPCFuture<IEaglerPlayerCape> getCapeByURL(@Nonnull String url, int timeoutSec);

	void sendRawCustomPayloadPacket(@Nonnull String channel, @Nonnull byte[] data);

}
