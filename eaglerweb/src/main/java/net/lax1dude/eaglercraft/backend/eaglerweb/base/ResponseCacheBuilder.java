package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.lax1dude.eaglercraft.backend.eaglerweb.base.EaglerWebConfig.ConfigDataMIMEType;

class ResponseCacheBuilder {

	private final ResponseCache cache;
	private final Map<File, ResponseCacheKey> map = new HashMap<>();
	private final Function<File, ConfigDataMIMEType> typeMapper;
	private final Function<File, ResponseCacheKey> factory;

	ResponseCacheBuilder(long expiresAfter, int maxCacheFiles, int threadCount, IEaglerWebLogger loggerIn, Function<File, ConfigDataMIMEType> mimes) {
		cache = new ResponseCache(expiresAfter, maxCacheFiles, threadCount, loggerIn);
		typeMapper = mimes;
		factory = (f) -> {
			return new ResponseCacheKey(f, typeMapper.apply(f));
		};
	}

	ResponseCacheKey createEntry(File file) {
		return map.computeIfAbsent(file, factory);
	}

	ResponseCache build() {
		return cache.start();
	}

}
