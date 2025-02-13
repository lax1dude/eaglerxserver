package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.UUID;
import java.util.function.Consumer;

public interface ISkinResolver {

	boolean isSkinDownloadEnabled();

	void resolveEaglerPlayerSkin(UUID playerUUID, Consumer<IEaglerPlayerSkin> callback);

	void resolveEaglerPlayerCape(UUID playerUUID, Consumer<IEaglerPlayerCape> callback);

	void loadCacheSkinFromURL(String skinURL, Consumer<IEaglerPlayerSkin> callback);

	void loadCacheCapeFromURL(String capeURL, Consumer<IEaglerPlayerCape> callback);

}
