package net.lax1dude.eaglercraft.backend.server.base.webserver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IPreparedResponse;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestHandler;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IWebServer;
import net.lax1dude.eaglercraft.backend.server.api.webserver.RouteDesc;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class WebServer implements IWebServer {

	private final EaglerXServer<?> server;
	private final Default404 default404;
	private final Default429 default429;
	private final Default500 default500;
	private final ReadWriteLock routeMapLock = new ReentrantReadWriteLock();
	private final RouteMap<IEaglerListenerInfo, IRequestHandler> routeMap;
	private final RouteProcessor registerProcessor = new RouteProcessor();
	private final Map<Object, Map<RouteDesc, IRequestHandler>> owners = new IdentityHashMap<>();
	private IRequestHandler handler404;
	private Object handler404Owner;
	private IRequestHandler handler429;
	private Object handler429Owner;
	private IRequestHandler handler500;
	private Object handler500Owner;

	public WebServer(EaglerXServer<?> server) {
		this.server = server;
		this.handler404 = this.default404 = new Default404(server);
		this.handler429 = this.default429 = new Default429(server);
		this.handler500 = this.default500 = new Default500(server);
		this.routeMap = new RouteMap<>();
	}

	public void refreshBuiltinPages() {
		default404.allocate(this);
		default429.allocate(this);
		default500.allocate(this);
	}

	public void releaseBuiltinPages() {
		default404.release();
		default429.release();
		default500.release();
	}

	@Override
	public void registerRoute(Object plugin, RouteDesc route, IRequestHandler requestHandler) {
		if(plugin == null) throw new IllegalArgumentException("Plugin must not be null!");
		if(route == null) throw new IllegalArgumentException("Route must not be null!");
		if(requestHandler == null) throw new IllegalArgumentException("Handler must not be null!");
		routeMapLock.writeLock().lock();
		try {
			if(route == RouteDesc.DEFAULT_404) {
				if(handler404Owner != null && handler404Owner != plugin) {
					throw new IllegalStateException("Default 404 handler is already managed by: " + handler404Owner.getClass().getName());
				}
				handler404.unbind(this);
				handler404 = requestHandler;
				handler404Owner = plugin;
				requestHandler.bind(this);
			}else if(route == RouteDesc.DEFAULT_429) {
				if(handler429Owner != null && handler429Owner != plugin) {
					throw new IllegalStateException("Default 429 handler is already managed by: " + handler429Owner.getClass().getName());
				}
				handler429.unbind(this);
				handler429 = requestHandler;
				handler429Owner = plugin;
				requestHandler.bind(this);
			}else if(route == RouteDesc.DEFAULT_500) {
				if(handler500Owner != null && handler500Owner != plugin) {
					throw new IllegalStateException("Default 500 handler is already managed by: " + handler500Owner.getClass().getName());
				}
				handler500.unbind(this);
				handler500 = requestHandler;
				handler500Owner = plugin;
				requestHandler.bind(this);
			}else {
				IEaglerListenerInfo info = null; 
				if(!route.isAllListeners()) {
					info = server.getListenerByName(route.getListenerName());
					if(info == null) {
						throw new IllegalArgumentException("Listener not found: " + route.getListenerName());
					}
				}
				if(route.isAllMethods()) {
					if(!registerProcessor.register(route.getPattern(), info, -1, routeMap, requestHandler)) {
						throw new IllegalStateException("Route map conflict for: \"" + route.getPattern() + "\" "
								+ (route.isAllListeners() ? ("(listener: [any], method: [any])")
										: ("(listener: \"" + route.getListenerName() + "\", method: [any])")));
					}
				}else {
					EnumRequestMethod[] meth = route.getMethods();
					for(int i = 0; i < meth.length; ++i) {
						if(!registerProcessor.register(route.getPattern(), info, meth[i].id(), routeMap, requestHandler)) {
							for(int j = 0; j < i; ++j) {
								registerProcessor.remove(route.getPattern(), info, meth[j].id(), routeMap, requestHandler);
							}
							throw new IllegalStateException("Route map conflict for: \"" + route.getPattern() + "\" "
									+ (route.isAllListeners() ? ("(listener: [any], method: " + meth[i] + ")")
											: ("(listener: \"" + route.getListenerName() + "\", method: " + meth[i] + ")")));
						}
					}
				}
				Map<RouteDesc, IRequestHandler> map = owners.get(plugin);
				if(map == null) {
					owners.put(plugin, map = new HashMap<>());
				}
				map.put(route, requestHandler);
			}
		}finally {
			routeMapLock.writeLock().unlock();
		}
	}

	@Override
	public void unregisterRoute(Object plugin, RouteDesc route) {
		if(plugin == null) throw new IllegalArgumentException("Plugin must not be null!");
		if(route == null) throw new IllegalArgumentException("Route must not be null!");
		routeMapLock.writeLock().lock();
		try {
			if(route == RouteDesc.DEFAULT_404) {
				if(handler404Owner != null && handler404Owner != plugin) {
					throw new IllegalStateException("Code 404 handler is already managed by: " + handler404Owner.getClass().getName());
				}
				handler404.unbind(this);
				handler404 = default404;
				handler404Owner = null;
			}else if(route == RouteDesc.DEFAULT_429) {
				if(handler429Owner != null && handler429Owner != plugin) {
					throw new IllegalStateException("Code 429 handler is already managed by: " + handler429Owner.getClass().getName());
				}
				handler429.unbind(this);
				handler429 = default429;
				handler429Owner = null;
			}else if(route == RouteDesc.DEFAULT_500) {
				if(handler500Owner != null && handler500Owner != plugin) {
					throw new IllegalStateException("Code 500 handler is already managed by: " + handler500Owner.getClass().getName());
				}
				handler500.unbind(this);
				handler500 = default500;
				handler500Owner = null;
			}else {
				IEaglerListenerInfo info = null; 
				if(!route.isAllListeners()) {
					info = server.getListenerByName(route.getListenerName());
					if(info == null) {
						throw new IllegalArgumentException("Listener not found: " + route.getListenerName());
					}
				}
				Map<RouteDesc, IRequestHandler> map = owners.get(plugin);
				if(map != null) {
					IRequestHandler handler = map.remove(route);
					if(handler == null) {
						Object owner = getCurrentOwner(route);
						if(owner != null) {
							throw new IllegalStateException("Handler for route \"" + route.getPattern() + "\" is already managed by: " + owner.getClass().getName());
						}else {
							throw new IllegalStateException("Handler for route \"" + route.getPattern() + "\" is already managed by another plugin");
						}
					}else {
						if(map.isEmpty()) {
							owners.remove(plugin);
						}
					}
					if(route.isAllMethods()) {
						registerProcessor.remove(route.getPattern(), info, -1, routeMap, handler);
					}else {
						EnumRequestMethod[] meth = route.getMethods();
						for(int i = 0; i < meth.length; ++i) {
							registerProcessor.remove(route.getPattern(), info, meth[i].id(), routeMap, handler);
						}
					}
				}else {
					throw new IllegalStateException("No routes are registered by the provided plugin");
				}
			}
		}finally {
			routeMapLock.writeLock().unlock();
		}
	}

	private Object getCurrentOwner(RouteDesc route) {
		for(Map.Entry<Object, Map<RouteDesc, IRequestHandler>> etr : owners.entrySet()) {
			if(etr.getValue().containsKey(route)) {
				return etr.getKey();
			}
		}
		return null;
	}

	@Override
	public synchronized void unregisterRoutes(Object plugin) {
		if(plugin == null) throw new IllegalArgumentException("Plugin must not be null!");
		routeMapLock.writeLock().lock();
		try {
			if(handler404Owner == plugin) {
				handler404.unbind(this);
				handler404 = default404;
				handler404Owner = null;
			}
			if(handler429Owner == plugin) {
				handler429.unbind(this);
				handler429 = default429;
				handler429Owner = null;
			}
			if(handler500Owner == plugin) {
				handler500.unbind(this);
				handler500 = default500;
				handler500Owner = null;
			}
			Map<RouteDesc, IRequestHandler> map = owners.get(plugin);
			if(map != null) {
				for(Map.Entry<RouteDesc, IRequestHandler> etr : map.entrySet()) {
					RouteDesc route = etr.getKey();
					IEaglerListenerInfo info = null; 
					if(!route.isAllListeners()) {
						info = server.getListenerByName(route.getListenerName());
						if(info == null) {
							throw new IllegalStateException("Registered listener not found: " + route.getListenerName());
						}
					}
					IRequestHandler handler = etr.getValue();
					if(route.isAllMethods()) {
						registerProcessor.remove(route.getPattern(), info, -1, routeMap, handler);
					}else {
						EnumRequestMethod[] meth = route.getMethods();
						for(int i = 0; i < meth.length; ++i) {
							registerProcessor.remove(route.getPattern(), info, meth[i].id(), routeMap, handler);
						}
					}
				}
			}
		}finally {
			routeMapLock.writeLock().unlock();
		}
	}

	@Override
	public IRequestHandler resolve(IEaglerListenerInfo listener, EnumRequestMethod method, CharSequence path) {
		return resolveInternal(listener, method.id(), path, new RouteProcessor()).result;
	}

	public RouteMap.Result<IRequestHandler> resolveInternal(IEaglerListenerInfo listener, int method, CharSequence path, RouteProcessor routeProcessor) {
		routeMapLock.readLock().lock();
		try {
			RouteMap.Result<IRequestHandler> res = routeProcessor.find(path, listener, method, routeMap);
			if(res.result != null) {
				return res;
			}
			res.directory = isDir(path);
			res.result = handler404;
			return res;
		}finally {
			routeMapLock.readLock().unlock();
		}
	}

	public RouteMap.Result<List<EnumRequestMethod>> optionsInternal(IEaglerListenerInfo listener, CharSequence path, RouteProcessor routeProcessor) {
		routeMapLock.readLock().lock();
		try {
			return routeProcessor.options(path, listener, routeMap);
		}finally {
			routeMapLock.readLock().unlock();
		}
	}

	private boolean isDir(CharSequence seq) {
		int l = seq.length();
		return l > 0 && seq.charAt(l - 1) == '/';
	}

	@Override
	public IRequestHandler getDefault404Handler() {
		return default404;
	}

	@Override
	public IRequestHandler get404Handler() {
		return handler404;
	}

	@Override
	public IRequestHandler getDefault429Handler() {
		return default429;
	}

	@Override
	public IRequestHandler get429Handler() {
		return handler429;
	}

	@Override
	public IRequestHandler getDefault500Handler() {
		return default500;
	}

	@Override
	public IRequestHandler get500Handler() {
		return handler500;
	}

	@Override
	public IPreparedResponse prepareResponse(InputStream data) throws IOException {
		ByteBuf buf = Unpooled.buffer();
		try {
			byte[] tmp = new byte[2048];
			int i;
			while((i = data.read(tmp)) != -1) {
				buf.writeBytes(tmp, 0, i);
			}
			return new PreparedResponse(buf.retain());
		}finally {
			buf.release();
		}
	}

	@Override
	public IPreparedResponse prepareResponse(byte[] data) {
		return new PreparedResponse(Unpooled.wrappedBuffer(data));
	}

	@Override
	public IPreparedResponse prepareResponse(CharSequence data, Charset binaryCharset) {
		ByteBuf buf = Unpooled.buffer();
		try {
			buf.writeCharSequence(data, binaryCharset);
			return new PreparedResponse(buf.retain());
		}finally {
			buf.release();
		}
	}

}
