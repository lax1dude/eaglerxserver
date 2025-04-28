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

package net.lax1dude.eaglercraft.backend.server.base;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.skin_cache.ISkinCacheService;

class DeferredStartSkinCache implements ISkinCacheService {

	private final List<Consumer<ISkinCacheService>> deferredTasks = new LinkedList<>();

	private ISkinCacheService service;

	void setDelegate(ISkinCacheService service) {
		this.service = service;
		if (service != null) {
			List<Consumer<ISkinCacheService>> lst;
			synchronized (deferredTasks) {
				lst = new ArrayList<>(deferredTasks);
				deferredTasks.clear();
			}
			if (!lst.isEmpty()) {
				for (Consumer<ISkinCacheService> consumer : lst) {
					consumer.accept(service);
				}
			}
		}
	}

	@Override
	public void resolveSkinByURL(String skinURL, Consumer<byte[]> callback) {
		ISkinCacheService svc = service;
		if (svc != null) {
			svc.resolveSkinByURL(skinURL, callback);
		} else {
			synchronized (deferredTasks) {
				deferredTasks.add((svc2) -> {
					svc2.resolveSkinByURL(skinURL, callback);
				});
			}
		}
	}

	@Override
	public void resolveCapeByURL(String capeURL, Consumer<byte[]> callback) {
		ISkinCacheService svc = service;
		if (svc != null) {
			svc.resolveCapeByURL(capeURL, callback);
		} else {
			synchronized (deferredTasks) {
				deferredTasks.add((svc2) -> {
					svc2.resolveCapeByURL(capeURL, callback);
				});
			}
		}
	}

	@Override
	public void tick() {
		ISkinCacheService svc = service;
		if (svc != null) {
			svc.tick();
		}
	}

	@Override
	public int getTotalMemorySkins() {
		ISkinCacheService svc = service;
		if (svc != null) {
			return svc.getTotalMemorySkins();
		} else {
			return 0;
		}
	}

	@Override
	public int getTotalMemoryCapes() {
		ISkinCacheService svc = service;
		if (svc != null) {
			return svc.getTotalMemoryCapes();
		} else {
			return 0;
		}
	}

	@Override
	public int getTotalStoredSkins() {
		ISkinCacheService svc = service;
		if (svc != null) {
			return svc.getTotalStoredSkins();
		} else {
			return 0;
		}
	}

	@Override
	public int getTotalStoredCapes() {
		ISkinCacheService svc = service;
		if (svc != null) {
			return svc.getTotalStoredCapes();
		} else {
			return 0;
		}
	}
}
