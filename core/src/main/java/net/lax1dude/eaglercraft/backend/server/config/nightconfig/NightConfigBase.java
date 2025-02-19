package net.lax1dude.eaglercraft.backend.server.config.nightconfig;

import java.io.File;

import net.lax1dude.eaglercraft.backend.server.config.EnumConfigFormat;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfSection;
import net.lax1dude.eaglercraft.backend.server.config.IEaglerConfig;

public class NightConfigBase implements IEaglerConfig {

	private NightConfigSection root;
	private boolean modified;

	public NightConfigBase(NightConfigSection root) {
		this.root = root;
		this.modified = false;
	}

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
	public boolean saveIfModified(File file) {
		// TODO Auto-generated method stub
		return false;
	}

}
