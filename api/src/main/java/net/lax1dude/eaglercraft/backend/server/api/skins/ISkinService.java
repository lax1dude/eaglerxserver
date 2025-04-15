package net.lax1dude.eaglercraft.backend.server.api.skins;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface ISkinService<PlayerObject> extends ISkinResolver {

	@Nonnull
	IEaglerXServerAPI<PlayerObject> getServerAPI();

	@Nullable
	default ISkinManagerBase<PlayerObject> getPlayer(@Nonnull PlayerObject player) {
		IBasePlayer<PlayerObject> ret = getServerAPI().getPlayer(player);
		return ret != null ? ret.getSkinManager() : null;
	}

	@Nullable
	default ISkinManagerEagler<PlayerObject> getEaglerPlayer(@Nonnull PlayerObject player) {
		IEaglerPlayer<PlayerObject> ret = getServerAPI().getEaglerPlayer(player);
		return ret != null ? ret.getSkinManager() : null;
	}

	@Nonnull
	default IProfileResolver getProfileResolver() {
		return getServerAPI().getProfileResolver();
	}

	ISkinImageLoader getSkinLoader(boolean cacheEnabled);

	default ISkinImageLoader getSkinLoader() {
		return getSkinLoader(true);
	}

	boolean isFNAWSkinsEnabledOnServer(String serverName);

}
