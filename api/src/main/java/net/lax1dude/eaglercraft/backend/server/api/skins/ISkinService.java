package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface ISkinService<PlayerObject> extends ISkinResolver, ISkinImageLoader {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	ISkinManagerBase<PlayerObject> getPlayer(PlayerObject player);

	ISkinManagerBase<PlayerObject> getPlayerByName(String playerName);

	ISkinManagerBase<PlayerObject> getPlayerByUUID(UUID playerUUID);

	ISkinManagerEagler<PlayerObject> getEaglerPlayer(PlayerObject player);

	ISkinManagerEagler<PlayerObject> getEaglerPlayerByName(String playerName);

	ISkinManagerEagler<PlayerObject> getEaglerPlayerByUUID(UUID playerUUID);

	void setEaglerPlayersVanillaSkin(String texturesPropertyValue, String texturesPropertySignature);

}
