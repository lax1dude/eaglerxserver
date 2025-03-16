package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.File;

public interface IEaglerWebPlatform<PlayerObject> {

	IEaglerWebLogger logger();

	File getDataFolder();

	void setHandleRefresh(IHandleRefresh handleRefresh);

	public interface IHandleRefresh {
		int refresh();
	}

}
