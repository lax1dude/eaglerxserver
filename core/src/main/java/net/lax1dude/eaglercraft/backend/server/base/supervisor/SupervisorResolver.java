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

package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.MapMaker;

import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistration;
import net.lax1dude.eaglercraft.backend.server.api.collect.IntProcedure;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings.ConfigDataSkinService;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingCape;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingSkin;

public class SupervisorResolver implements ISupervisorResolverImpl {

	public static final long FOREIGN_LOOKUP_TIMEOUT = 15000l * 1000000l; // Safe default

	private final SupervisorService<?> service;
	private final LoadingCache<String, ForeignSkin> foreignSkinCache;
	private final LoadingCache<String, ForeignCape> foreignCapeCache;
	private final ConcurrentMap<UUID, PendingSkinLookup> pendingSkinLookups;
	private final ConcurrentMap<UUID, PendingCapeLookup> pendingCapeLookups;
	private List<IDeferredLoad> deferred = new LinkedList<>();

	SupervisorResolver(SupervisorService<?> service) {
		this.service = service;
		ConfigDataSkinService conf = service.getEaglerXServer().getConfig().getSettings().getSkinService();
		this.foreignSkinCache = CacheBuilder.newBuilder()
				.expireAfterAccess(conf.getSkinCacheMemoryKeepSeconds(), TimeUnit.SECONDS)
				.initialCapacity(Math.min(1024, conf.getSkinCacheMemoryMaxObjects()))
				.maximumSize(conf.getSkinCacheMemoryMaxObjects()).concurrencyLevel(16)
				.build(new CacheLoader<String, ForeignSkin>() {
					@Override
					public ForeignSkin load(String key) throws Exception {
						return new ForeignSkin(SupervisorResolver.this, key);
					}
				});
		this.foreignCapeCache = CacheBuilder.newBuilder()
				.expireAfterAccess(conf.getSkinCacheMemoryKeepSeconds(), TimeUnit.SECONDS)
				.initialCapacity(Math.min(1024, conf.getSkinCacheMemoryMaxObjects()))
				.maximumSize(conf.getSkinCacheMemoryMaxObjects()).concurrencyLevel(16)
				.build(new CacheLoader<String, ForeignCape>() {
					@Override
					public ForeignCape load(String key) throws Exception {
						return new ForeignCape(SupervisorResolver.this, key);
					}
				});
		this.pendingSkinLookups = (new MapMaker()).initialCapacity(256).concurrencyLevel(16).makeMap();
		this.pendingCapeLookups = (new MapMaker()).initialCapacity(256).concurrencyLevel(16).makeMap();
	}

	SupervisorConnection getConnection() {
		return service.getConnection();
	}

	ForeignSkin getForeignSkin(String url) {
		try {
			return foreignSkinCache.get(url);
		} catch (ExecutionException e) {
			if(e.getCause() instanceof RuntimeException ee) throw ee;
			throw new RuntimeException(e.getCause());
		}
	}

	ForeignCape getForeignCape(String url) {
		try {
			return foreignCapeCache.get(url);
		} catch (ExecutionException e) {
			if(e.getCause() instanceof RuntimeException ee) throw ee;
			throw new RuntimeException(e.getCause());
		}
	}

	@Override
	public boolean isPlayerKnown(UUID playerUUID) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		SupervisorConnection conn = service.getConnection();
		if(conn != null) {
			return conn.remotePlayers.containsKey(playerUUID);
		}
		return false;
	}

	@Override
	public int getCachedNodeId(UUID playerUUID) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		SupervisorConnection conn = service.getConnection();
		if(conn != null) {
			SupervisorPlayer player = conn.remotePlayers.get(playerUUID);
			if(player != null) {
				return player.getNodeId();
			}
		}
		return -1;
	}

	@Override
	public void resolvePlayerNodeId(UUID playerUUID, IntProcedure callback) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		SupervisorConnection conn = service.getConnection();
		if(conn != null) {
			SupervisorPlayer player = conn.loadPlayer(playerUUID);
			int node = player.getNodeId();
			if(node != -1) {
				callback.apply(node);
			}else {
				player.loadBrandUUID(null, (trash) -> {
					if(trash != null) {
						callback.apply(player.getNodeId());
					}else {
						callback.apply(-1);
					}
				});
			}
		}else {
			callback.apply(-1);
		}
	}

	@Override
	public void resolvePlayerBrand(UUID playerUUID, Consumer<UUID> callback) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		SupervisorConnection conn = service.getConnection();
		if(conn != null) {
			conn.loadPlayer(playerUUID).loadBrandUUID(null, callback);
		}else {
			callback.accept(null);
		}
	}

	@Override
	public void resolvePlayerRegisteredBrand(UUID playerUUID, BiConsumer<UUID, IBrandRegistration> callback) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		SupervisorConnection conn = service.getConnection();
		if(conn != null) {
			conn.loadPlayer(playerUUID).loadBrandUUID(null, (uuid) -> {
				if(uuid != null) {
					callback.accept(uuid, service.getEaglerXServer().getBrandService().lookupRegisteredBrand(uuid));
				}else {
					callback.accept(null, null);
				}
			});
		}else {
			callback.accept(null, null);
		}
	}

	@Override
	public boolean isSkinDownloadEnabled() {
		return true;
	}

	@Override
	public IEaglerPlayerSkin getSkinNotFound(UUID playerUUID) {
		return MissingSkin.forPlayerUUID(playerUUID);
	}

	@Override
	public IEaglerPlayerCape getCapeNotFound() {
		return MissingCape.MISSING_CAPE;
	}

	@Override
	public void resolvePlayerSkin(UUID playerUUID, Consumer<IEaglerPlayerSkin> callback) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		SupervisorConnection conn = service.getConnection();
		if(conn != null) {
			conn.loadPlayer(playerUUID).loadSkinData(null, callback);
		}else {
			callback.accept(MissingSkin.UNAVAILABLE_SKIN);
		}
	}

	@Override
	public void resolvePlayerCape(UUID playerUUID, Consumer<IEaglerPlayerCape> callback) {
		if(playerUUID == null) {
			throw new NullPointerException("playerUUID");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		SupervisorConnection conn = service.getConnection();
		if(conn != null) {
			conn.loadPlayer(playerUUID).loadCapeData(null, callback);
		}else {
			callback.accept(MissingCape.UNAVAILABLE_CAPE);
		}
	}

	@Override
	public void loadCacheSkinFromURL(String skinURL, EnumSkinModel modelId, Consumer<IEaglerPlayerSkin> callback) {
		if(skinURL == null) {
			throw new NullPointerException("skinURL");
		}
		if(modelId == null) {
			throw new NullPointerException("modelId");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		getForeignSkin(skinURL).load(modelId.getId(), null, callback);
	}

	@Override
	public void loadCacheCapeFromURL(String capeURL, Consumer<IEaglerPlayerCape> callback) {
		if(capeURL == null) {
			throw new NullPointerException("capeURL");
		}
		if(callback == null) {
			throw new NullPointerException("callback");
		}
		getForeignCape(capeURL).load(null, callback);
	}

	@Override
	public void resolvePlayerSkinKeyed(UUID requester, UUID playerUUID, Consumer<IEaglerPlayerSkin> callback) {
		SupervisorConnection conn = service.getConnection();
		if(conn != null) {
			conn.loadPlayer(playerUUID).loadSkinData(requester, callback);
		}else {
			callback.accept(MissingSkin.UNAVAILABLE_SKIN);
		}
	}

	@Override
	public void resolvePlayerCapeKeyed(UUID requester, UUID playerUUID, Consumer<IEaglerPlayerCape> callback) {
		SupervisorConnection conn = service.getConnection();
		if(conn != null) {
			conn.loadPlayer(playerUUID).loadCapeData(requester, callback);
		}else {
			callback.accept(MissingCape.UNAVAILABLE_CAPE);
		}
	}

	@Override
	public void resolvePlayerBrandKeyed(UUID requester, UUID playerUUID, Consumer<UUID> callback) {
		SupervisorConnection conn = service.getConnection();
		if(conn != null) {
			conn.loadPlayer(playerUUID).loadBrandUUID(requester, callback);
		}else {
			callback.accept(ISupervisorResolverImpl.UNAVAILABLE);
		}
	}

	@Override
	public void resolveForeignSkinKeyed(UUID requester, int modelId, String skinURL,
			Consumer<IEaglerPlayerSkin> callback) {
		getForeignSkin(skinURL).load(modelId, requester, callback);
	}

	@Override
	public void resolveForeignCapeKeyed(UUID requester, String capeURL, Consumer<IEaglerPlayerCape> callback) {
		getForeignCape(capeURL).load(requester, callback);
	}

	private class PendingSkinLookup implements ISupervisorExpiring {

		private final UUID requestUUID;
		private final long expiresAt;
		private final Consumer<IEaglerPlayerSkin> consumer;

		protected PendingSkinLookup(UUID requestUUID, long expiresAt, Consumer<IEaglerPlayerSkin> consumer) {
			this.requestUUID = requestUUID;
			this.expiresAt = expiresAt;
			this.consumer = consumer;
		}

		@Override
		public long expiresAt() {
			return expiresAt;
		}

		@Override
		public void expire() {
			if(pendingSkinLookups.remove(requestUUID) != null) {
				try {
					consumer.accept(MissingSkin.UNAVAILABLE_SKIN);
				}catch(Exception ex) {
					service.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}

	}

	void addWaitingForeignURLSkinLookup(UUID requestUUID, Consumer<IEaglerPlayerSkin> callback) {
		long now = System.nanoTime();
		PendingSkinLookup lookup = new PendingSkinLookup(requestUUID, now + FOREIGN_LOOKUP_TIMEOUT, callback);
		pendingSkinLookups.put(requestUUID, lookup);
		service.timeoutLoop().addFuture(now, lookup);
	}

	private class PendingCapeLookup implements ISupervisorExpiring {

		private final UUID requestUUID;
		private final long expiresAt;
		private final Consumer<IEaglerPlayerCape> consumer;

		protected PendingCapeLookup(UUID requestUUID, long expiresAt, Consumer<IEaglerPlayerCape> consumer) {
			this.requestUUID = requestUUID;
			this.expiresAt = expiresAt;
			this.consumer = consumer;
		}

		@Override
		public long expiresAt() {
			return expiresAt;
		}

		@Override
		public void expire() {
			if(pendingCapeLookups.remove(requestUUID) != null) {
				try {
					consumer.accept(MissingCape.UNAVAILABLE_CAPE);
				}catch(Exception ex) {
					service.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}

	}

	void addWaitingForeignURLCapeLookup(UUID requestUUID, Consumer<IEaglerPlayerCape> callback) {
		long now = System.nanoTime();
		PendingCapeLookup lookup = new PendingCapeLookup(requestUUID, now + FOREIGN_LOOKUP_TIMEOUT, callback);
		pendingCapeLookups.put(requestUUID, lookup);
		service.timeoutLoop().addFuture(now, lookup);
	}

	public boolean onForeignSkinReceived(UUID requestUUID, IEaglerPlayerSkin skin) {
		PendingSkinLookup lookup = pendingSkinLookups.remove(requestUUID);
		if(lookup != null) {
			try {
				lookup.consumer.accept(skin);
			}catch(Exception ex) {
				service.logger().error("Caught error from lazy load callback", ex);
			}
			return true;
		}
		return false;
	}

	public boolean onForeignCapeReceived(UUID requestUUID, IEaglerPlayerCape cape) {
		PendingCapeLookup lookup = pendingCapeLookups.remove(requestUUID);
		if(lookup != null) {
			try {
				lookup.consumer.accept(cape);
			}catch(Exception ex) {
				service.logger().error("Caught error from lazy load callback", ex);
			}
			return true;
		}
		return false;
	}

	interface IDeferredLoad {
		void complete(boolean fail);
	}

	void addDeferred(IDeferredLoad runnable) {
		eag: {
			eagler: synchronized(this) {
				if(deferred == null) {
					break eag;
				}
				if(getConnection() != null) {
					break eagler;
				}
				deferred.add(runnable);
				return;
			}
			runnable.complete(false);
			return;
		}
		runnable.complete(true);
	}

	void flushDeferred() {
		List<IDeferredLoad> lst;
		synchronized(this) {
			if(deferred == null) {
				return;
			}
			lst = new ArrayList<>(deferred);
			deferred = null;
		}
		for(IDeferredLoad run : lst) {
			run.complete(false);
		}
	}

	void onConnectionEnd() {
		Object[] arr = pendingSkinLookups.values().toArray();
		for(int i = 0; i < arr.length; ++i) {
			((ISupervisorExpiring)arr[i]).expire();
		}
		pendingSkinLookups.clear();
		arr = pendingCapeLookups.values().toArray();
		for(int i = 0; i < arr.length; ++i) {
			((ISupervisorExpiring)arr[i]).expire();
		}
		pendingCapeLookups.clear();
	}

}
