package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.UUID;
import java.util.function.Consumer;

public interface ISkinResolver {

	boolean isSkinDownloadEnabled();

	default IEaglerPlayerSkin getSkinNotFound() {
		return getSkinNotFound(null);
	}

	IEaglerPlayerSkin getSkinNotFound(UUID playerUUID);

	IEaglerPlayerCape getCapeNotFound();

	void resolvePlayerSkin(UUID playerUUID, Consumer<IEaglerPlayerSkin> callback);

	void resolvePlayerCape(UUID playerUUID, Consumer<IEaglerPlayerCape> callback);

	default void loadCacheSkinFromURL(String skinURL, Consumer<IEaglerPlayerSkin> callback) {
		loadCacheSkinFromURL(skinURL, EnumSkinModel.STEVE, callback);
	}

	void loadCacheSkinFromURL(String skinURL, EnumSkinModel modelId, Consumer<IEaglerPlayerSkin> callback);

	void loadCacheCapeFromURL(String capeURL, Consumer<IEaglerPlayerCape> callback);

}
