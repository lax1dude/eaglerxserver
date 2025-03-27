package net.lax1dude.eaglercraft.backend.server.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class RateLimiterMap<K> {

	private final LoadingCache<K, RateLimiter> cache;

	public RateLimiterMap() {
		cache = CacheBuilder.newBuilder().expireAfterAccess(5l, TimeUnit.MINUTES)
				.maximumSize(8192).build(new CacheLoader<K, RateLimiter>() {
					@Override
					public RateLimiter load(K arg0) throws Exception {
						return new RateLimiter();
					}
				});
	}

	public boolean rateLimit(K key, int limit) {
		try {
			return cache.get(key).rateLimit(limit);
		} catch (ExecutionException e) {
			Throwables.throwIfUnchecked(e.getCause());
			throw new RuntimeException(e);
		}
	}

}
