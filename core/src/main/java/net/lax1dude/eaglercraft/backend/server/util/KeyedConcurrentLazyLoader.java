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

package net.lax1dude.eaglercraft.backend.server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lax1dude.eaglercraft.backend.server.api.collect.ObjectIntMap;
import net.lax1dude.eaglercraft.backend.server.base.collect.ObjectIntHashMap;

public abstract class KeyedConcurrentLazyLoader<K, T> {

	private static final Logger logger = LoggerFactory.getLogger("KeyedConcurrentLazyLoader");

	public static class KeyedConsumerList<K, T> {

		private final ObjectIntMap<K> map = new ObjectIntHashMap<>(8);
		private final List<Consumer<T>> list = new ArrayList<>(8);

		public KeyedConsumerList() {
		}

		public void add(K key, Consumer<T> value) {
			if(key != null) {
				int idx = map.indexOf(key);
				if(idx >= 0) {
					list.set(map.indexGet(idx), value);
				}else {
					int i = list.size();
					map.addTo(key, i);
					list.add(value);
				}
			}else {
				list.add(value);
			}
		}

		public List<Consumer<T>> getList() {
			return list;
		}

	}

	private KeyedConsumerList<K, T> waitingCallbacks = null;
	protected T result = null;

	protected abstract void loadImpl(Consumer<T> callback);

	public void load(K key, Consumer<T> callback) {
		T val = result;
		if(val != null) {
			callback.accept(val);
		}else {
			eag: synchronized(this) {
				val = result;
				if(val != null) {
					break eag;
				}
				if(waitingCallbacks == null) {
					waitingCallbacks = new KeyedConsumerList<>();
					waitingCallbacks.add(key, callback);
				}else {
					waitingCallbacks.add(key, callback);
					return;
				}
			}
			if(val != null) {
				callback.accept(val);
				return;
			}
			loadImpl((res) -> {
				if(res == null) {
					throw new NullPointerException("result must not be null");
				}
				KeyedConsumerList<K, T> toCall;
				synchronized(this) {
					if(result != null) {
						return; // ignore multiple results
					}
					result = res;
					toCall = waitingCallbacks;
					waitingCallbacks = null;
				}
				if(toCall != null) {
					List<Consumer<T>> toCallList = toCall.getList();
					for(int i = 0, l = toCallList.size(); i < l; ++i) {
						try {
							toCallList.get(i).accept(res);
						}catch(Exception ex) {
							logger.error("Caught error from lazy load callback", ex);
						}
					}
				}
			});
		}
	}

	public T getIfLoaded() {
		return result;
	}

	public void clear() {
		result = null;
	}

}