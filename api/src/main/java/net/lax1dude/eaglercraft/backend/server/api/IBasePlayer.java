package net.lax1dude.eaglercraft.backend.server.api;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistration;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinManagerBase;

public interface IBasePlayer<PlayerObject> extends IBasePendingConnection {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	PlayerObject getPlayerObject();

	String getMinecraftBrand();

	UUID getEaglerBrandUUID();

	default IBrandRegistration getEaglerBrandDesc() {
		return getServerAPI().getBrandRegistry().lookupRegisteredBrand(getEaglerBrandUUID());
	}

	boolean isConnected();

	boolean isOnlineMode();

	IEaglerPlayer<PlayerObject> asEaglerPlayer();

	ISkinManagerBase<PlayerObject> getSkinManager();

	<ComponentObject> void disconnect(ComponentObject kickMessage);

}
