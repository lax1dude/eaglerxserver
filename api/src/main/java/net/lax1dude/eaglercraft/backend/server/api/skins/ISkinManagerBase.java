package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;

public interface ISkinManagerBase<PlayerObject> {

	@Nonnull
	IBasePlayer<PlayerObject> getPlayer();

	@Nonnull
	ISkinService<PlayerObject> getSkinService();

	boolean isEaglerPlayer();

	@Nullable
	ISkinManagerEagler<PlayerObject> asEaglerPlayer();

	@Nullable
	IEaglerPlayerSkin getPlayerSkinIfLoaded();

	@Nullable
	IEaglerPlayerCape getPlayerCapeIfLoaded();

	void resolvePlayerSkin(@Nonnull Consumer<IEaglerPlayerSkin> callback);

	void resolvePlayerCape(@Nonnull Consumer<IEaglerPlayerCape> callback);

	void resolvePlayerTextures(@Nonnull BiConsumer<IEaglerPlayerSkin, IEaglerPlayerCape> callback);

	default void changePlayerSkin(@Nonnull IEaglerPlayerSkin newSkin) {
		changePlayerSkin(newSkin, true);
	}

	void changePlayerSkin(@Nonnull IEaglerPlayerSkin newSkin, boolean notifyOthers);

	default void changePlayerSkin(@Nonnull EnumPresetSkins newSkin) {
		changePlayerSkin(newSkin, true);
	}

	void changePlayerSkin(@Nonnull EnumPresetSkins newSkin, boolean notifyOthers);

	default void changePlayerCape(@Nonnull IEaglerPlayerCape newCape) {
		changePlayerCape(newCape, true);
	}

	void changePlayerCape(@Nonnull IEaglerPlayerCape newCape, boolean notifyOthers);

	default void changePlayerCape(@Nonnull EnumPresetCapes newCape) {
		changePlayerCape(newCape, true);
	}

	void changePlayerCape(@Nonnull EnumPresetCapes newCape, boolean notifyOthers);

	default void changePlayerTextures(@Nonnull IEaglerPlayerSkin newSkin, @Nonnull IEaglerPlayerCape newCape) {
		changePlayerTextures(newSkin, newCape, true);
	}

	void changePlayerTextures(@Nonnull IEaglerPlayerSkin newSkin, @Nonnull IEaglerPlayerCape newCape, boolean notifyOthers);

	default void changePlayerTextures(@Nonnull EnumPresetSkins newSkin, @Nonnull EnumPresetCapes newCape) {
		changePlayerTextures(newSkin, newCape, true);
	}

	void changePlayerTextures(@Nonnull EnumPresetSkins newSkin, @Nonnull EnumPresetCapes newCape, boolean notifyOthers);

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

}
