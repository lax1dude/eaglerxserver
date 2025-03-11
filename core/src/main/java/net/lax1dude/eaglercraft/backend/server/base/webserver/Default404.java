package net.lax1dude.eaglercraft.backend.server.base.webserver;

import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

class Default404 extends DefaultHandler {

	protected Default404(EaglerXServer<?> server) {
		super(server);
	}

	@Override
	protected int getCode() {
		return 404;
	}

	@Override
	protected String getContents(EaglerXServer<?> server) {
		return "<h1>404 Not Found</h1>"; //TODO
	}

}
