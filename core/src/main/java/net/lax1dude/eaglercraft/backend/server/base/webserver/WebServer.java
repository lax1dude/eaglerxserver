package net.lax1dude.eaglercraft.backend.server.base.webserver;

import java.io.InputStream;
import java.nio.charset.Charset;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
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
	}

	public void provisionBuiltinPages() {
		default404.provision(this);
		default429.provision(this);
		default500.provision(this);
	}

	public void releaseBuiltinPages() {
		default404.release();
		default429.release();
		default500.release();
	}

	@Override
	public synchronized void registerRoute(Object plugin, RouteDesc route, IRequestHandler requestHandler) {
		if(plugin == null) throw new IllegalArgumentException("Plugin must not be null!");
		if(route == null) throw new IllegalArgumentException("Route must not be null!");
		if(requestHandler == null) throw new IllegalArgumentException("Handler must not be null!");
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
			//TODO
		}
	}

	@Override
	public synchronized void unregisterRoute(Object plugin, RouteDesc route) {
		if(plugin == null) throw new IllegalArgumentException("Plugin must not be null!");
		if(route == null) throw new IllegalArgumentException("Route must not be null!");
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
			//TODO
		}
	}

	@Override
	public synchronized void unregisterRoutes(Object plugin) {
		if(plugin == null) throw new IllegalArgumentException("Plugin must not be null!");
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
		//TODO
	}

	@Override
	public IRequestHandler resolve(String listenerName, EnumRequestMethod method, String path) {
		// TODO Auto-generated method stub
		return null;
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
	public IPreparedResponse prepareResponse(InputStream data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPreparedResponse prepareResponse(byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPreparedResponse prepareResponse(String data, Charset binaryCharset) {
		// TODO Auto-generated method stub
		return null;
	}

}
