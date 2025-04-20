package net.lax1dude.eaglercraft.backend.server.config.snakeyaml;

import java.io.File;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;

public class YAMLConfigLoader {

	private static final boolean MODERN;

	static {
		boolean b = false;
		try {
			Class.forName("org.yaml.snakeyaml.LoaderOptions").getMethod("setProcessComments", boolean.class);
			b = true;
		}catch(ReflectiveOperationException ex) {
		}
		MODERN = b;
	}

	public static IEaglerConfig getConfigFile(File file) throws IOException {
		if(MODERN) {
			return net.lax1dude.eaglercraft.backend.server.config.snakeyaml.modern.YAMLConfigLoader.getConfigFile(file);
		}else {
			return net.lax1dude.eaglercraft.backend.server.config.snakeyaml.legacy.YAMLConfigLoader.getConfigFile(file);
		}
	}

}
