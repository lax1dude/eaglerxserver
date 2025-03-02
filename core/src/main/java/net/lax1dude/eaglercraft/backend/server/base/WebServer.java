package net.lax1dude.eaglercraft.backend.server.base;

import java.io.InputStream;
import java.nio.charset.Charset;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IPreparedResponse;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestHandler;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IWebServer;
import net.lax1dude.eaglercraft.backend.server.api.webserver.RouteDesc;

public class WebServer implements IWebServer {

	private final EaglerXServer<?> server;

	public WebServer(EaglerXServer<?> server) {
		this.server = server;
	}

	@Override
	public void registerRoute(Object plugin, RouteDesc route, IRequestHandler requestHandler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterRoute(Object plugin, RouteDesc route) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterRoutes(Object plugin) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IRequestHandler resolve(String listenerName, EnumRequestMethod method, String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRequestHandler getDefault404Handler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRequestHandler get404Handler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRequestHandler getDefault429Handler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRequestHandler get429Handler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRequestHandler getDefault500Handler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRequestHandler get500Handler() {
		// TODO Auto-generated method stub
		return null;
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
