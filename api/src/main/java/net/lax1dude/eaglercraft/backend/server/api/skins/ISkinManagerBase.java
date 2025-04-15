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

	void changePlayerSkin(@Nonnull IEaglerPlayerSkin newSkin, boolean notifyOthers);

	void changePlayerSkin(@Nonnull EnumPresetSkins newSkin, boolean notifyOthers);

	void changePlayerCape(@Nonnull IEaglerPlayerCape newCape, boolean notifyOthers);

	void changePlayerCape(@Nonnull EnumPresetCapes newCape, boolean notifyOthers);

	void changePlayerTextures(@Nonnull IEaglerPlayerSkin newSkin, @Nonnull IEaglerPlayerCape newCape, boolean notifyOthers);

	void changePlayerTextures(@Nonnull EnumPresetSkins newSkin, @Nonnull EnumPresetCapes newCape, boolean notifyOthers);

	void resetPlayerSkin(boolean notifyOthers);

	void resetPlayerCape(boolean notifyOthers);

	void resetPlayerTextures(boolean notifyOthers);

}
