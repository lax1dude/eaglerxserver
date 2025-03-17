package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.File;

import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWebConfig.ConfigDataMIMEType;

class ResponseCacheKey {

	private final File file;
	private final ConfigDataMIMEType type;

	ResponseCacheKey(File file, ConfigDataMIMEType type) {
		this.file = file;
		this.type = type;
	}

	File getFile() {
		return file;
	}

	ConfigDataMIMEType getType() {
		return type;
	}

}
