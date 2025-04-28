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

package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectObjectMap;
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
			synchronized (KeyedRequestHelper.this) {
				cs = consumer;
				if (internalMap != null && internalMap.remove(url) != null && internalMap.isEmpty()) {
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
		if (now - lastFlush > 30l * 1000000000l) {
			lastFlush = now;
			if (internalMap != null) {
				flush(now);
			}
		}
		if (internalMap == null) {
			internalMap = new ObjectObjectHashMap<>();
			KeyedRequest req = new KeyedRequest(url, now);
			req.consumer = consumer;
			internalMap.put(url, req);
			return req;
		} else {
			KeyedRequest req = internalMap.get(url);
			if (req == null) {
				req = new KeyedRequest(url, now);
				req.consumer = consumer;
				internalMap.put(url, req);
				return req;
			} else {
				req.consumer = consumer;
				return null;
			}
		}
	}

	private void flush(long now) {
		internalMap.removeAll((k, v) -> now - v.createdAt > 30l * 1000000000l);
	}

}
