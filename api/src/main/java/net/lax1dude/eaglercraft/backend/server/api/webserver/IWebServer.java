package net.lax1dude.eaglercraft.backend.server.api.webserver;

import java.io.InputStream;
import java.nio.charset.Charset;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;

public interface IWebServer {

	void registerRoute(Object plugin, RouteDesc route, IRequestHandler requestHandler);

	void unregisterRoute(Object plugin, RouteDesc route);

	void unregisterRoutes(Object plugin);

	IRequestHandler resolve(String listenerName, EnumRequestMethod method, String path);

	IRequestHandler getDefault404Handler();

	IRequestHandler get404Handler();

	IRequestHandler getDefault429Handler();

	IRequestHandler get429Handler();

	IRequestHandler getDefault500Handler();

	IRequestHandler get500Handler();

	IPreparedResponse prepareResponse(InputStream data);

	IPreparedResponse prepareResponse(byte[] data);

	IPreparedResponse prepareResponse(String data, Charset binaryCharset);

}
