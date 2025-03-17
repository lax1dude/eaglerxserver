package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.util.List;

abstract class IndexNode {

	protected abstract IndexNode find(CharSequence charSeq);

	protected abstract boolean isDirectory();

	protected abstract ResponseCacheKey getResponse(List<String> index);

}