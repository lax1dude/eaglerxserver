package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.collect.IntIndexedContainer;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectCursor;
import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectObjectMap;
import net.lax1dude.eaglercraft.backend.server.base.collect.IntArrayList;
import net.lax1dude.eaglercraft.backend.server.base.collect.ObjectObjectHashMap;

class KeyedRequestHelper<T> {

	private class KeyedRequest implements Consumer<T> {

		protected final String url;
		protected final long createdAt;
		protected Consumer<T> consumer;

		protected KeyedRequest(String url, long createdAt) {
			this.url = url;
			this.createdAt = createdAt;
		}

		@Override
		public void accept(T t) {
			Consumer<T> cs;
			synchronized(KeyedRequestHelper.this) {
				cs = consumer;
				if(internalMap != null && internalMap.remove(url) != null && internalMap.isEmpty()) {
					internalMap = null;
				}
			}
			cs.accept(t);
		}

	}

	private ObjectObjectMap<String, KeyedRequest> internalMap = null;
	private long lastFlush = System.nanoTime();

	synchronized Consumer<T> add(String url, Consumer<T> consumer) {
		long now = System.nanoTime();
		if(now - lastFlush > 30l * 1000000000l) {
			lastFlush = now;
			if(internalMap != null) {
				flush(now);
			}
		}
		if(internalMap == null) {
			internalMap = new ObjectObjectHashMap<>();
			KeyedRequest req = new KeyedRequest(url, now);
			req.consumer = consumer;
			internalMap.put(url, req);
			return req;
		}else {
			KeyedRequest req = internalMap.get(url);
			if(req == null) {
				req = new KeyedRequest(url, now);
				req.consumer = consumer;
				internalMap.put(url, req);
				return req;
			}else {
				req.consumer = consumer;
				return null;
			}
		}
	}

	private void flush(long now) {
		IntIndexedContainer toRemove = null;
		for(ObjectCursor<KeyedRequestHelper<T>.KeyedRequest> cur : internalMap.values()) {
			if(now - cur.value.createdAt > 30l * 1000000000l) {
				if(toRemove == null) {
					toRemove = new IntArrayList();
				}
				toRemove.add(cur.index);
			}
		}
		if(toRemove != null) {
			toRemove.forEach(internalMap::indexRemove);
			if(internalMap.isEmpty()) {
				internalMap = null;
			}
		}
	}

}
