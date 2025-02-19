package net.lax1dude.eaglercraft.backend.server.config.nightconfig;

import java.io.File;
import java.io.IOException;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;

public class NightConfigLoader {

	public static IEaglerConfig getConfigFile(File file) throws IOException {
		CommentedFileConfig config;
		try {
			config = CommentedFileConfig.builder(file).preserveInsertionOrder()
					.onFileNotFound(FileNotFoundAction.READ_NOTHING).build();
			try {
				config.load();
			}finally {
				config.close();
			}
		}catch(Exception ex) {
			throw new IOException("Failed to load config file: " + file.getAbsolutePath());
		}
		return getConfigFile(config);
	}

	public static IEaglerConfig getConfigFile(CommentedConfig config) {
		NightConfigBase ret = new NightConfigBase();
		ret.root = new NightConfigSection(ret, config, null, config.size() > 0);
		return ret;
	}

	public static void writeConfigFile(CommentedConfig configIn, File file) throws IOException {
		try {
			CommentedFileConfig config = CommentedFileConfig.builder(file).preserveInsertionOrder().build();
			config.addAll(configIn);
			try {
				config.save();
			}finally {
				config.close();
			}
		}catch(Exception ex) {
			throw new IOException("Failed to save config file: " + file.getAbsolutePath());
		}
	}

}
