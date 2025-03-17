package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.File;
import java.io.IOException;

public interface IEaglerWebPlatform<PlayerObject> {

	IEaglerWebLogger logger();

	String getVersionString();

	File getDataFolder();

	void setHandleRefresh(IHandleRefresh handleRefresh);

	public interface IHandleRefresh {
		int refresh() throws IOException;
	}

}
