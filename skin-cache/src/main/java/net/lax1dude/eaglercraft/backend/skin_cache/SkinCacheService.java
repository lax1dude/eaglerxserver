/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.skin_cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.lax1dude.eaglercraft.backend.util.ConcurrentLazyLoader;
import net.lax1dude.eaglercraft.backend.util.ILoggerAdapter;

public class SkinCacheService implements ISkinCacheService {

	protected final ILoggerAdapter logger;
	protected final ISkinCacheDownloader downloader;
	protected final ISkinCacheDatastore datastore;

	protected final LoadingCache<String, ConcurrentLazyLoader<byte[]>> skinCache;
	protected final LoadingCache<String, ConcurrentLazyLoader<byte[]>> capeCache;

	protected long lastFlush = 0l;
	protected final ReadWriteLock failedLookupsLock = new ReentrantReadWriteLock();
	protected final Map<String, Long> failedLookups = new HashMap<>();

	private class SkinCacheEntry extends ConcurrentLazyLoader<byte[]> {

		protected final String key;

		protected SkinCacheEntry(String key) {
			this.key = key;
		}

		@Override
		protected void loadImpl(Consumer<byte[]> callback) {
			datastore.loadSkin(key, (data) -> {
				if(data != null) {
					callback.accept(data);
				}else {
					downloader.downloadSkin(key, (ddata) -> {
						if(ddata != null) {
							datastore.storeSkin(key, ddata);
							callback.accept(ddata);
						}else {
							long millis = System.nanoTime() / 1000000l;
							failedLookupsLock.writeLock().lock();
							try {
								failedLookups.put(key, millis);
							}finally {
								failedLookupsLock.writeLock().unlock();
							}
							callback.accept(ISkinCacheService.ERROR);
						}
					});
				}
			});
		}

		@Override
		protected ILoggerAdapter getLogger() {
			return logger;
		}

	}

	private class CapeCacheEntry extends ConcurrentLazyLoader<byte[]> {

		protected final String key;

		protected CapeCacheEntry(String key) {
			this.key = key;
		}

		@Override
		protected void loadImpl(Consumer<byte[]> callback) {
			datastore.loadCape(key, (data) -> {
				if(data != null) {
					callback.accept(data);
				}else {
					downloader.downloadCape(key, (ddata) -> {
						if(ddata != null) {
							datastore.storeCape(key, ddata);
							callback.accept(ddata);
						}else {
							long millis = System.nanoTime() / 1000000l;
							failedLookupsLock.writeLock().lock();
							try {
								failedLookups.put(key, millis);
							}finally {
								failedLookupsLock.writeLock().unlock();
							}
							callback.accept(ISkinCacheService.ERROR);
						}
					});
				}
			});
		}

		@Override
		protected ILoggerAdapter getLogger() {
			return logger;
		}

	}

	public SkinCacheService(ISkinCacheDownloader downloader, ISkinCacheDatastore datastore, int expireAfterSec,
			int maxSize, ILoggerAdapter logger) {
		this(downloader, datastore, expireAfterSec, Math.min(256, maxSize), maxSize, logger);
	}

	public SkinCacheService(ISkinCacheDownloader downloader, ISkinCacheDatastore datastore, int expireAfterSec,
			int initialSize, int maxSize, ILoggerAdapter logger) {
		this.logger = logger;
		this.downloader = downloader;
		this.datastore = datastore;
		this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(expireAfterSec, TimeUnit.SECONDS)
				.initialCapacity(initialSize).maximumSize(maxSize).concurrencyLevel(16)
				.build(new CacheLoader<String, ConcurrentLazyLoader<byte[]>>() {
					@Override
					public ConcurrentLazyLoader<byte[]> load(String key) throws Exception {
						return new SkinCacheEntry(key);
					}
				});
		this.capeCache = CacheBuilder.newBuilder().expireAfterAccess(expireAfterSec, TimeUnit.SECONDS)
				.initialCapacity(initialSize).maximumSize(maxSize).concurrencyLevel(16)
				.build(new CacheLoader<String, ConcurrentLazyLoader<byte[]>>() {
					@Override
					public ConcurrentLazyLoader<byte[]> load(String key) throws Exception {
						return new CapeCacheEntry(key);
					}
				});
	}

	@Override
	public void resolveSkinByURL(String skinURL, Consumer<byte[]> callback) {
		failedLookupsLock.readLock().lock();
		boolean b;
		try {
			b = failedLookups.containsKey(skinURL);
		}finally {
			failedLookupsLock.readLock().unlock();
		}
		if(b) {
			callback.accept(ISkinCacheService.ERROR);
		}else {
			try {
				skinCache.get(skinURL).load(callback);
			} catch (ExecutionException e) {
				callback.accept(ISkinCacheService.ERROR);
			}
		}
	}

	@Override
	public void resolveCapeByURL(String capeURL, Consumer<byte[]> callback) {
		failedLookupsLock.readLock().lock();
		boolean b;
		try {
			b = failedLookups.containsKey(capeURL);
		}finally {
			failedLookupsLock.readLock().unlock();
		}
		if(b) {
			callback.accept(ISkinCacheService.ERROR);
		}else {
			try {
				capeCache.get(capeURL).load(callback);
			} catch (ExecutionException e) {
				callback.accept(ISkinCacheService.ERROR);
			}
		}
	}

	@Override
	public void tick() {
		datastore.tick();
		long millis = System.nanoTime() / 1000000l;
		if(millis - lastFlush > 60000l) {
			lastFlush = millis;
			failedLookupsLock.writeLock().lock();
			try {
				Iterator<Long> itr = failedLookups.values().iterator();
				while(itr.hasNext()) {
					if(millis - itr.next().longValue() > (60000l * 10l)) {
						itr.remove();
					}
				}
			}finally {
				failedLookupsLock.writeLock().unlock();
			}
		}
	}

	@Override
	public int getTotalMemorySkins() {
		return (int)skinCache.size();
	}

	@Override
	public int getTotalMemoryCapes() {
		return (int)capeCache.size();
	}

	@Override
	public int getTotalStoredSkins() {
		return datastore.getTotalStoredSkins();
	}

	@Override
	public int getTotalStoredCapes() {
		return datastore.getTotalStoredCapes();
	}

}