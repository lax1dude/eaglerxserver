package net.lax1dude.eaglercraft.backend.server.api;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistration;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;

public interface IBasePlayer<PlayerObject> extends IBaseLoginConnection {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	PlayerObject getPlayerObject();

	String getMinecraftBrand();

	UUID getEaglerBrandUUID();

	default IBrandRegistration getEaglerBrandDesc() {
		return getServerAPI().getBrandRegistry().lookupRegisteredBrand(getEaglerBrandUUID());
	}

	IEaglerPlayer<PlayerObject> asEaglerPlayer();

	ISkinManagerBase<PlayerObject> getSkinManager();

	<ComponentObject> void disconnect(ComponentObject kickMessage);

}
