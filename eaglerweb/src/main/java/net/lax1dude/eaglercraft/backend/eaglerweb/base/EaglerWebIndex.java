package net.lax1dude.eaglercraft.backend.eaglerweb.base;

import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestContext;
import net.lax1dude.eaglercraft.backend.server.api.webserver.IRequestHandler;

public class EaglerWebIndex implements IRequestHandler {

	public static EaglerWebIndex build(EaglerWeb<?> eaglerWeb) {
		return null;
	}

	@Override
	public void handleRequest(IRequestContext requestContext) {
		
	}

	public void release() {
		
	}

	public int size() {
		return 0;
	}

}
