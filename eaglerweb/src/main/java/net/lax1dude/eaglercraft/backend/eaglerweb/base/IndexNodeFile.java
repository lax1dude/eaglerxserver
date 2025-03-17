package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.util.List;

class IndexNodeFile extends IndexNode {

	protected final ResponseCacheKey file;

	protected IndexNodeFile(ResponseCacheKey file) {
		this.file = file;
	}

	@Override
	protected IndexNode find(CharSequence charSeq) {
		return null;
	}

	@Override
	protected boolean isDirectory() {
		return false;
	}

	@Override
	protected ResponseCacheKey getResponse(List<String> index) {
		return file;
	}

}