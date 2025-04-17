package net.lax1dude.eaglercraft.backend.server.api.brand;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IBrandService<PlayerObject> extends IBrandResolver, IBrandRegistry {

	@Nonnull
	IEaglerXServerAPI<PlayerObject> getServerAPI();

	@Nonnull
	default UUID getPlayerBrand(@Nonnull PlayerObject player) {
		IBasePlayer<PlayerObject> basePlayer = getServerAPI().getPlayer(player);
		return basePlayer != null ? basePlayer.getEaglerBrandUUID() : BRAND_VANILLA;
	}

	@Nonnull
	default UUID getPlayerBrand(@Nonnull IBasePlayer<PlayerObject> player) {
		return player.getEaglerBrandUUID();
	}

	@Nullable
	default IBrandRegistration getPlayerRegisteredBrand(@Nonnull PlayerObject player) {
		IBasePlayer<PlayerObject> basePlayer = getServerAPI().getPlayer(player);
		return lookupRegisteredBrand(basePlayer != null ? basePlayer.getEaglerBrandUUID() : BRAND_VANILLA);
	}

}
