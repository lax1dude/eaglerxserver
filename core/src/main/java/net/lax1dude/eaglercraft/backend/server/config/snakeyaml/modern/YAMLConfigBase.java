package net.lax1dude.eaglercraft.backend.server.config.snakeyaml.modern;

import java.io.File;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.config.EnumConfigFormat;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;

public class YAMLConfigBase implements IEaglerConfig {

	final File file;
	YAMLConfigSection root;
	boolean modified;

	YAMLConfigBase(File file) {
		this.file = file;
	}

	@Override
	public EnumConfigFormat getFormat() {
		return EnumConfigFormat.YAML;
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
			YAMLConfigLoader.writeConfigFile(root.yaml, file);
			return true;
		}else {
			return false;
		}
	}

}
