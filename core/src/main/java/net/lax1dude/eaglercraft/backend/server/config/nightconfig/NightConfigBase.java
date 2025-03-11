package net.lax1dude.eaglercraft.backend.server.config.nightconfig;

import java.io.File;
import java.io.IOException;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.lax1dude.eaglercraft.backend.server.config.EnumConfigFormat;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;

public class NightConfigBase implements IEaglerConfig {

	NightConfigSection root;
	boolean modified;

	@Override
	public EnumConfigFormat getFormat() {
		return EnumConfigFormat.TOML;
	}

	@Override
	public IEaglerConfSection getRoot() {
		return root;
	}

	@Override
	public boolean isModified() {
		return modified;
	}

	@Override
	public boolean saveIfModified() throws IOException {
		if(modified) {
			NightConfigLoader.writeConfigFile((CommentedFileConfig)root.config);
			modified = false;
			return true;
		}else {
			return false;
		}
	}

}
