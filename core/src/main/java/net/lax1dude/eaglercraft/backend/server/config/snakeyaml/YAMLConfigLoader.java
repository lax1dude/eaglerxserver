package net.lax1dude.eaglercraft.backend.server.config.snakeyaml;

import java.io.File;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class YAMLConfigLoader {

	private static final boolean MODERN = Util.classExists("org.yaml.snakeyaml.LoaderOptions");

	public static IEaglerConfig getConfigFile(File file) throws IOException {
		if(MODERN) {
			return net.lax1dude.eaglercraft.backend.server.config.snakeyaml.modern.YAMLConfigLoader.getConfigFile(file);
		}else {
			return net.lax1dude.eaglercraft.backend.server.config.snakeyaml.legacy.YAMLConfigLoader.getConfigFile(file);
		}
	}

}
