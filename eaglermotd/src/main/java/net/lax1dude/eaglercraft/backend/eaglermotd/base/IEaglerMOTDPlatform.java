package net.lax1dude.eaglercraft.backend.eaglermotd.base;

import java.io.File;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftMOTDEvent;

public interface IEaglerMOTDPlatform<PlayerObject> {

	IEaglerMOTDLogger logger();

	void setOnMOTD(Consumer<IEaglercraftMOTDEvent<PlayerObject>> handler);

	File getDataFolder();

}
