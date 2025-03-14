package net.lax1dude.eaglercraft.backend.server.api.webserver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;

public interface IWebServer {

	void registerRoute(Object plugin, RouteDesc route, IRequestHandler requestHandler);

	void unregisterRoute(Object plugin, RouteDesc route);

	void unregisterRoutes(Object plugin);

	IRequestHandler resolve(IEaglerListenerInfo listener, EnumRequestMethod method, CharSequence path);

	IRequestHandler getDefault404Handler();

	IRequestHandler get404Handler();

	IRequestHandler getDefault429Handler();

	IRequestHandler get429Handler();

	IRequestHandler getDefault500Handler();

	IRequestHandler get500Handler();

	IPreparedResponse prepareResponse(InputStream data) throws IOException;

	IPreparedResponse prepareResponse(byte[] data);

	IPreparedResponse prepareResponse(CharSequence data, Charset binaryCharset);

}
