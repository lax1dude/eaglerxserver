package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class ResponseCacheBuilder {

	private final ResponseCache cache;
	private final Map<File, ResponseCacheKey> map = new HashMap<>();

	ResponseCacheBuilder(long expiresAfter, int maxCacheFiles, int threadCount, IEaglerWebLogger loggerIn) {
		cache = new ResponseCache(expiresAfter, maxCacheFiles, threadCount, loggerIn);
	}

	ResponseCacheKey createEntry(File file) {
		return map.computeIfAbsent(file, ResponseCacheKey::new);
	}

	ResponseCache build() {
		return cache.start();
	}

}
