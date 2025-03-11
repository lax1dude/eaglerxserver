package net.lax1dude.eaglercraft.backend.server.config.nightconfig;

import java.io.File;
import java.io.IOException;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;

import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;
import net.lax1dude.eaglercraft.backend.server.config.WrapUtil;

public class NightConfigLoader {

	public static final int TOML_COMMENT_WRAP = 80;

	public static IEaglerConfig getConfigFile(File file) throws IOException {
		CommentedFileConfig config = null;
		try {
			config = CommentedFileConfig.builder(file).sync().preserveInsertionOrder()
					.onFileNotFound(FileNotFoundAction.READ_NOTHING).build();
			config.load();
		}catch(Exception ex) {
			try {
				if(config != null) {
					config.close();
				}
			}catch(Exception exx) {
			}
			throw new IOException("Failed to load config file: " + file.getAbsolutePath());
		}
		return getConfigFile(config);
	}

	public static IEaglerConfig getConfigFile(CommentedConfig config) {
		NightConfigBase ret = new NightConfigBase();
		ret.root = new NightConfigSection(ret, config, null, config.size() > 0);
		return ret;
	}

	public static void writeConfigFile(CommentedFileConfig configIn) throws IOException {
		File p = configIn.getFile().getAbsoluteFile().getParentFile();
		if(p != null && !p.isDirectory() && !p.mkdirs()) {
			throw new IOException("Could not create directory: " + p.getAbsolutePath());
		}
		try {
			try {
				configIn.save();
			}finally {
				configIn.close();
			}
		}catch(Exception ex) {
			throw new IOException("Failed to save config file: " + configIn.getFile().getAbsolutePath(), ex);
		}
	}

	public static String createComment(String stringIn) {
		if(stringIn != null) {
			return " " + WrapUtil.wrap(stringIn, TOML_COMMENT_WRAP, System.lineSeparator() + " ", false, " ");
		}else {
			return null;
		}
	}

}
