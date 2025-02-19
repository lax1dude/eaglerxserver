package net.lax1dude.eaglercraft.backend.server.base.config;

import java.io.IOException;

public interface IConfigDirectory {

	<T> T loadConfig(String fileName, IConfigLoadFunction<T> func) throws IOException;

}
