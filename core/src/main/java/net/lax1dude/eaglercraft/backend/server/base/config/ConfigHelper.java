package net.lax1dude.eaglercraft.backend.server.base.config;

import java.io.File;
import java.util.function.Function;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatform;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;

public class ConfigHelper {

	public static <T> T getConfigDirectory(IPlatform<?> platform, Function<IConfigDirectory, T> handler) {
		String singleFile = System.getProperty("eaglerxserver.singleConfigFile");
		if(singleFile != null) {
			File f = new File(singleFile);
			platform.logger().info("Using single config file at: " + f.getAbsolutePath());
			IEaglerConfig conf = platform.getConfigFile(f);
			T result = handler.apply(new IConfigDirectory() {
				@Override
				public <V> V loadConfig(String fileName, IConfigLoadFunction<V> func) {
					return func.call(conf.getRoot().getSection(fileName));
				}
			});
			if(conf.saveIfModified(f)) {
				platform.logger().info("Config file was updated: " + f.getAbsolutePath());
			}
			return result;
		}else {
			return handler.apply(new IConfigDirectory() {
				@Override
				public <V> V loadConfig(String fileName, IConfigLoadFunction<V> func) {
					File f = new File(platform.getDataFolder(), fileName + "." + platform.getConfigFormat());
					IEaglerConfig conf = platform.getConfigFile(f);
					V ret = func.call(conf.getRoot());
					if(conf.saveIfModified(f)) {
						platform.logger().info("Config file was updated: " + f.getAbsolutePath());
					}
					return ret;
				}
			});
		}
	}

}
