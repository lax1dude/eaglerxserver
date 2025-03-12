package net.lax1dude.eaglercraft.backend.server.base.webserver;

import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.server.api.webserver.IPreparedResponse;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestContext;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestHandler;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

abstract class DefaultHandler implements IRequestHandler {

	private final EaglerXServer<?> server;
	private IPreparedResponse response;

	protected DefaultHandler(EaglerXServer<?> server) {
		this.server = server;
	}

	@Override
	public void handleRequest(IRequestContext requestContext) {
		requestContext.setResponseCode(getCode());
		if(response != null) {
			requestContext.setResponseBody(response);
		}else {
			requestContext.setResponseBody(getContents(server), StandardCharsets.UTF_8);
		}
	}

	void allocate(WebServer webServer) {
		release();
		response = webServer.prepareResponse(getContents(server), StandardCharsets.UTF_8);
	}

	void release() {
		if(response != null) {
			response.release();
			response = null;
		}
	}

	protected abstract int getCode();

	protected abstract String getContents(EaglerXServer<?> server);

}
