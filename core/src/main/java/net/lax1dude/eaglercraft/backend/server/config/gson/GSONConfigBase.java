package net.lax1dude.eaglercraft.backend.server.config.gson;

import java.io.File;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.server.config.EnumConfigFormat;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;

public class GSONConfigBase implements IEaglerConfig {

	final File file;
	GSONConfigSection root;
	boolean modified;

	GSONConfigBase(File file) {
		this.file = file;
	}

	@Override
	public EnumConfigFormat getFormat() {
		return EnumConfigFormat.JSON;
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
			GSONConfigLoader.writeConfigFile(root.json, file);
			return true;
		}else {
			return false;
		}
	}

}
