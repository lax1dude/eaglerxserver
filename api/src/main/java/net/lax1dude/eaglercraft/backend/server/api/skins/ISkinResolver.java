package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ISkinResolver {

	boolean isSkinDownloadEnabled();

	@Nonnull
	default IEaglerPlayerSkin getSkinNotFound() {
		return getSkinNotFound(null);
	}

	@Nonnull
	IEaglerPlayerSkin getSkinNotFound(@Nullable UUID playerUUID);

	@Nonnull
	IEaglerPlayerCape getCapeNotFound();

	void resolvePlayerSkin(@Nonnull UUID playerUUID, @Nonnull Consumer<IEaglerPlayerSkin> callback);

	void resolvePlayerCape(@Nonnull UUID playerUUID, @Nonnull Consumer<IEaglerPlayerCape> callback);

	default void loadCacheSkinFromURL(@Nonnull String skinURL, @Nonnull Consumer<IEaglerPlayerSkin> callback) {
		loadCacheSkinFromURL(skinURL, EnumSkinModel.STEVE, callback);
	}

	void loadCacheSkinFromURL(@Nonnull String skinURL, @Nonnull EnumSkinModel modelId,
			@Nonnull Consumer<IEaglerPlayerSkin> callback);

	void loadCacheCapeFromURL(@Nonnull String capeURL, @Nonnull Consumer<IEaglerPlayerCape> callback);

}
