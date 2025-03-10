package net.lax1dude.eaglercraft.backend.server.api.brand;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IBrandService<PlayerObject> extends IBrandResolver, IBrandRegistry {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	default UUID getPlayerBrand(PlayerObject player) {
		IBasePlayer<PlayerObject> basePlayer = getServerAPI().getPlayer(player);
		return basePlayer != null ? basePlayer.getEaglerBrandUUID() : BRAND_VANILLA;
	}

	default UUID getPlayerBrand(IBasePlayer<PlayerObject> player) {
		return player.getEaglerBrandUUID();
	}

	default IBrandRegistration getPlayerRegisteredBrand(PlayerObject player) {
		IBasePlayer<PlayerObject> basePlayer = getServerAPI().getPlayer(player);
		return lookupRegisteredBrand(basePlayer != null ? basePlayer.getEaglerBrandUUID() : BRAND_VANILLA);
	}

	default IBrandRegistration getPlayerRegisteredBrand(IBasePlayer<PlayerObject> player) {
		return lookupRegisteredBrand(player.getEaglerBrandUUID());
	}

}
