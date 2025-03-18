package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.util.Date;
import java.util.List;

abstract class IndexNode {

	protected IndexNode parent;

	abstract IndexNode find(CharSequence charSeq);

	abstract boolean isDirectory();

	abstract Date lastModified();

	abstract ResponseCacheKey getResponse(List<String> index);

	abstract String getName();

	abstract long getSize();

	IndexNode getParent() {
		return parent;
	}

}