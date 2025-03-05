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
		if(service != null) {
			List<Consumer<ISkinCacheService>> lst;
			synchronized(deferredTasks) {
				lst = new ArrayList<>(deferredTasks);
				deferredTasks.clear();
			}
			if(!lst.isEmpty()) {
				for(Consumer<ISkinCacheService> consumer : lst) {
					consumer.accept(service);
				}
			}
		}
	}

	@Override
	public void resolveSkinByURL(String skinURL, Consumer<byte[]> callback) {
		ISkinCacheService svc = service;
		if(svc != null) {
			svc.resolveSkinByURL(skinURL, callback);
		}else {
			synchronized(deferredTasks) {
				deferredTasks.add((svc2) -> {
					svc2.resolveSkinByURL(skinURL, callback);
				});
			}
		}
	}

	@Override
	public void resolveCapeByURL(String capeURL, Consumer<byte[]> callback) {
		ISkinCacheService svc = service;
		if(svc != null) {
			svc.resolveCapeByURL(capeURL, callback);
		}else {
			synchronized(deferredTasks) {
				deferredTasks.add((svc2) -> {
					svc2.resolveCapeByURL(capeURL, callback);
				});
			}
		}
	}

	@Override
	public void tick() {
		ISkinCacheService svc = service;
		if(svc != null) {
			svc.tick();
		}
	}

	@Override
	public int getTotalMemorySkins() {
		ISkinCacheService svc = service;
		if(svc != null) {
			return svc.getTotalMemorySkins();
		}else {
			return 0;
		}
	}

	@Override
	public int getTotalMemoryCapes() {
		ISkinCacheService svc = service;
		if(svc != null) {
			return svc.getTotalMemoryCapes();
		}else {
			return 0;
		}
	}

	@Override
	public int getTotalStoredSkins() {
		ISkinCacheService svc = service;
		if(svc != null) {
			return svc.getTotalStoredSkins();
		}else {
			return 0;
		}
	}

	@Override
	public int getTotalStoredCapes() {
		ISkinCacheService svc = service;
		if(svc != null) {
			return svc.getTotalStoredCapes();
		}else {
			return 0;
		}
	}
}
