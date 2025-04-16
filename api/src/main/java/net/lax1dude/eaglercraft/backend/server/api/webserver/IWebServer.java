package net.lax1dude.eaglercraft.backend.server.api.webserver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;

public interface IWebServer {

	void registerRoute(@Nonnull Object plugin, @Nonnull RouteDesc route, @Nonnull IRequestHandler requestHandler);

	void unregisterRoute(@Nonnull Object plugin, @Nonnull RouteDesc route);

	void unregisterRoutes(@Nonnull Object plugin);

	@Nonnull
	IRequestHandler resolve(@Nonnull IEaglerListenerInfo listener, @Nonnull EnumRequestMethod method,
			@Nonnull CharSequence path);

	@Nonnull
	IRequestHandler getDefault404Handler();

	@Nonnull
	IRequestHandler get404Handler();

	@Nonnull
	IRequestHandler getDefault429Handler();

	@Nonnull
	IRequestHandler get429Handler();

	@Nonnull
	IRequestHandler getDefault500Handler();

	@Nonnull
	IRequestHandler get500Handler();

	@Nonnull
	IPreparedResponse prepareResponse(@Nonnull InputStream data) throws IOException;

	@Nonnull
	IPreparedResponse prepareResponse(@Nonnull byte[] data);

	@Nonnull
	IPreparedResponse prepareResponse(@Nonnull CharSequence data, @Nonnull Charset binaryCharset);

}
