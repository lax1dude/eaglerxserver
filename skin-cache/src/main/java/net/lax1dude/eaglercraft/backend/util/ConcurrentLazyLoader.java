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

package net.lax1dude.eaglercraft.backend.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ConcurrentLazyLoader<T> {

	private List<Consumer<T>> waitingCallbacks = null;
	private T result = null;

	protected abstract void loadImpl(Consumer<T> callback);

	protected abstract ILoggerAdapter getLogger();

	public void load(Consumer<T> callback) {
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
					waitingCallbacks = new ArrayList<>();
					waitingCallbacks.add(callback);
				}else {
					waitingCallbacks.add(callback);
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
				List<Consumer<T>> toCall;
				synchronized(this) {
					if(result != null) {
						return; // ignore multiple results
					}
					result = res;
					toCall = waitingCallbacks;
					waitingCallbacks = null;
				}
				if(toCall != null) {
					for(int i = 0, l = toCall.size(); i < l; ++i) {
						try {
							toCall.get(i).accept(res);
						}catch(Exception ex) {
							getLogger().error("Caught error from lazy load callback", ex);
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