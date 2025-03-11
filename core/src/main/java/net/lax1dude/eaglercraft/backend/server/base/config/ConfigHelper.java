package net.lax1dude.eaglercraft.backend.server.base.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatform;
import net.lax1dude.eaglercraft.backend.server.config.EnumConfigFormat;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;

public class ConfigHelper {

	private static final List<EnumConfigFormat> PREFERRED_ORDER = Arrays.asList(EnumConfigFormat.TOML,
			EnumConfigFormat.YAML, EnumConfigFormat.JSON);

	private final Set<EnumConfigFormat> supported;
	private final Map<String, EnumConfigFormat> fromExtension;
	private final EnumConfigFormat preferred;

	public ConfigHelper(IPlatform<?> platform) {
		supported = platform.getConfigFormats();
		EnumConfigFormat pref = null;
		shit: {
			for(EnumConfigFormat fmt : PREFERRED_ORDER) {
				if(supported.contains(fmt)) {
					pref = fmt;
					break shit;
				}
			}
			if(!supported.isEmpty()) {
				pref = supported.iterator().next();
				break shit;
			}
			throw new IllegalStateException("No supported config formats on this platform!");
		}
		preferred = pref;
		fromExtension = new HashMap<>();
		for(EnumConfigFormat fmt : supported) {
			for(String ext : fmt.getExts()) {
				fromExtension.put(ext, fmt);
			}
		}
	}

	public interface IConfigDirectoryLoader<T> {
		T load(IConfigDirectory dir) throws IOException;
	}

	public <T> T getConfigDirectory(IPlatform<?> platform, IConfigDirectoryLoader<T> handler) throws IOException {
		String singleFile = System.getProperty("eaglerxserver.singleConfigFile");
		String formatProperty = System.getProperty("eaglerxserver.configFormat");
		final EnumConfigFormat defaultFormat;
		if(formatProperty != null) {
			defaultFormat = fromExtension.get(formatProperty.toLowerCase());
			if(defaultFormat == null) {
				throw new UnsupportedOperationException("Unknown eaglerxserver.configFormat: " + formatProperty);
			}
		}else {
			if(singleFile != null) {
				int idx = singleFile.lastIndexOf('.');
				if(idx != -1) {
					defaultFormat = fromExtension.getOrDefault(singleFile.substring(idx + 1), preferred);
				}else {
					defaultFormat = preferred;
				}
			}else {
				defaultFormat = preferred;
			}
		}
		if(singleFile != null) {
			File f = new File(singleFile);
			platform.logger().info("Using single config file at: " + f.getAbsolutePath());
			IEaglerConfig conf = defaultFormat.getConfigFile(f);
			T result = handler.load(new IConfigDirectory() {
				@Override
				public <V> V loadConfig(String fileName, IConfigLoadFunction<V> func) throws IOException {
					return func.call(conf.getRoot().getSection(fileName));
				}
			});
			if(conf.saveIfModified()) {
				platform.logger().info("Config file was updated: " + f.getAbsolutePath());
			}
			return result;
		}else {
			return handler.load(new IConfigDirectory() {
				@Override
				public <V> V loadConfig(String fileName, IConfigLoadFunction<V> func) throws IOException {
					EnumConfigFormat fmt = defaultFormat;
					File dataFolder = platform.getDataFolder();
					File f = new File(dataFolder, fileName + "." + fmt.getDefaultExt());
					if(!f.isFile()) {
						search: for(EnumConfigFormat fmt2 : supported) {
							if(fmt != fmt2) {
								for(String s : fmt2.getExts()) {
									File f2 = new File(dataFolder, fileName + "." + s);
									if(f2.isFile()) {
										fmt = fmt2;
										f = f2;
										break search;
									}
								}
							}
						}
					}
					IEaglerConfig conf = fmt.getConfigFile(f);
					V ret = func.call(conf.getRoot());
					if(conf.saveIfModified()) {
						platform.logger().info("Config file was updated: " + f.getAbsolutePath());
					}
					return ret;
				}
			});
		}
	}

}
