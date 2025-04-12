package net.lax1dude.eaglercraft.backend.server.api.skins;

import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface ISkinService<PlayerObject> extends ISkinResolver {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	default ISkinManagerBase<PlayerObject> getPlayer(PlayerObject player) {
		IBasePlayer<PlayerObject> ret = getServerAPI().getPlayer(player);
		return ret != null ? ret.getSkinManager() : null;
	}

	default ISkinManagerEagler<PlayerObject> getEaglerPlayer(PlayerObject player) {
		IEaglerPlayer<PlayerObject> ret = getServerAPI().getEaglerPlayer(player);
		return ret != null ? ret.getSkinManager() : null;
	}

	default IProfileResolver getProfileResolver() {
		return getServerAPI().getProfileResolver();
	}

	ISkinImageLoader getSkinLoader(boolean cacheEnabled);

	default ISkinImageLoader getSkinLoader() {
		return getSkinLoader(true);
	}

	boolean isFNAWSkinsEnabledOnServer(String serverName);

}
