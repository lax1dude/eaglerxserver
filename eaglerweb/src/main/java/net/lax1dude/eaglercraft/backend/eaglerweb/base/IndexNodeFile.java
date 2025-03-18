package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.File;
import java.util.Date;
import java.util.List;

class IndexNodeFile extends IndexNode {

	private final ResponseCacheKey file;
	private final String name;
	private final Date dateObj;
	private final long size;

	protected IndexNodeFile(ResponseCacheKey file) {
		this.file = file;
		File f = file.getFile();
		this.name = f.getName();
		this.dateObj = new Date(file.getLastModified());
		this.size = f.length();
	}

	@Override
	IndexNode find(CharSequence charSeq) {
		return null;
	}

	@Override
	boolean isDirectory() {
		return false;
	}

	@Override
	ResponseCacheKey getResponse(List<String> index) {
		return file;
	}

	@Override
	String getName() {
		return name;
	}

	@Override
	Date lastModified() {
		return dateObj;
	}

	@Override
	long getSize() {
		return size;
	}

}