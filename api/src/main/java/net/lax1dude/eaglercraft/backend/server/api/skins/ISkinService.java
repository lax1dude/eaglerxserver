package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface ISkinService<PlayerObject> extends ISkinResolver {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	default ISkinManagerBase<PlayerObject> getPlayer(PlayerObject player) {
		IBasePlayer<PlayerObject> ret = getServerAPI().getPlayer(player);
		return ret != null ? ret.getSkinManager() : null;
	}

	default ISkinManagerBase<PlayerObject> getPlayerByName(String playerName) {
		IBasePlayer<PlayerObject> ret = getServerAPI().getPlayerByName(playerName);
		return ret != null ? ret.getSkinManager() : null;
	}

	default ISkinManagerBase<PlayerObject> getPlayerByUUID(UUID playerUUID) {
		IBasePlayer<PlayerObject> ret = getServerAPI().getPlayerByUUID(playerUUID);
		return ret != null ? ret.getSkinManager() : null;
	}

	default ISkinManagerEagler<PlayerObject> getEaglerPlayer(PlayerObject player) {
		IEaglerPlayer<PlayerObject> ret = getServerAPI().getEaglerPlayer(player);
		return ret != null ? ret.getSkinManager() : null;
	}

	default ISkinManagerEagler<PlayerObject> getEaglerPlayerByName(String playerName) {
		IEaglerPlayer<PlayerObject> ret = getServerAPI().getEaglerPlayerByName(playerName);
		return ret != null ? ret.getSkinManager() : null;
	}

	default ISkinManagerEagler<PlayerObject> getEaglerPlayerByUUID(UUID playerUUID) {
		IEaglerPlayer<PlayerObject> ret = getServerAPI().getEaglerPlayerByUUID(playerUUID);
		return ret != null ? ret.getSkinManager() : null;
	}

	void setEaglerPlayersVanillaSkin(String texturesPropertyValue, String texturesPropertySignature);

	ISkinImageLoader getSkinLoader(boolean cacheEnabled);

	default ISkinImageLoader getSkinLoader() {
		return getSkinLoader(true);
	}

}
