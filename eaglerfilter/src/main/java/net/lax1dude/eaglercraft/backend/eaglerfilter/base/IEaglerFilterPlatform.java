package net.lax1dude.eaglercraft.backend.eaglerfilter.base;

import java.io.File;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftClientBrandEvent;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftWebSocketOpenEvent;

public interface IEaglerFilterPlatform<PlayerObject, ComponentObject> {

	IEaglerFilterLogger logger();

	void setOnWebSocketOpen(Consumer<IEaglercraftWebSocketOpenEvent<PlayerObject>> handler);

	void setOnClientBrand(Consumer<IEaglercraftClientBrandEvent<PlayerObject, ComponentObject>> handler);

	File getDataFolder();

}
