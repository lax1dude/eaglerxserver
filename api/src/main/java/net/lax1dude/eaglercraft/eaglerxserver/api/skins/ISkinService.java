package net.lax1dude.eaglercraft.eaglerxserver.api.skins;

import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.eaglerxserver.api.IEaglerXServerAPI;

public interface ISkinService<PlayerObject> extends ISkinImageLoader {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	ISkinManagerBase<PlayerObject> getPlayer(PlayerObject player);

	ISkinManagerBase<PlayerObject> getPlayerByName(String playerName);

	ISkinManagerBase<PlayerObject> getPlayerByUUID(UUID playerUUID);

	ISkinManagerEagler<PlayerObject> getEaglerPlayer(PlayerObject player);

	ISkinManagerEagler<PlayerObject> getEaglerPlayerByName(String playerName);

	ISkinManagerEagler<PlayerObject> getEaglerPlayerByUUID(UUID playerUUID);

	boolean isDownloadEnabled();

	void loadCacheSkinFromURL(String skinURL, Consumer<IEaglerPlayerSkin> callback);

	void loadCacheCapeFromURL(String capeURL, Consumer<IEaglerPlayerCape> callback);

}
