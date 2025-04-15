package net.lax1dude.eaglercraft.backend.server.api;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistration;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;

public interface IBasePlayer<PlayerObject> extends IBaseLoginConnection {

	@Nonnull
	IEaglerXServerAPI<PlayerObject> getServerAPI();

	@Nonnull
	PlayerObject getPlayerObject();

	@Nullable
	String getMinecraftBrand();

	@Nonnull
	UUID getEaglerBrandUUID();

	@Nullable
	default IBrandRegistration getEaglerBrandDesc() {
		return getServerAPI().getBrandService().lookupRegisteredBrand(getEaglerBrandUUID());
	}

	@Nullable
	IEaglerPlayer<PlayerObject> asEaglerPlayer();

	@Nonnull
	ISkinManagerBase<PlayerObject> getSkinManager();

	<ComponentObject> void disconnect(@Nonnull ComponentObject kickMessage);

}
