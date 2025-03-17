package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.File;

class ResponseCacheKey {

	private final File file;

	ResponseCacheKey(File file) {
		this.file = file;
	}

	File getFile() {
		return file;
	}

}
