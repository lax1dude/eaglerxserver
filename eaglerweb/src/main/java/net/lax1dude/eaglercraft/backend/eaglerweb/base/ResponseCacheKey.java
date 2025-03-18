package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.File;

import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWebConfig.ConfigDataMIMEType;

class ResponseCacheKey {

	private final File file;
	private final long lastModified;
	private final ConfigDataMIMEType type;

	ResponseCacheKey(File file, ConfigDataMIMEType type) {
		this.file = file;
		this.lastModified = file.lastModified();
		this.type = type;
	}

	File getFile() {
		return file;
	}

	long getLastModified() {
		return lastModified;
	}

	ConfigDataMIMEType getType() {
		return type;
	}

}
