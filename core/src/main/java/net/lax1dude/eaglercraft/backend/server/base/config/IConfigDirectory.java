package net.lax1dude.eaglercraft.backend.server.base.config;

public interface IConfigDirectory {

	<T> T loadConfig(String fileName, IConfigLoadFunction<T> func);

}
