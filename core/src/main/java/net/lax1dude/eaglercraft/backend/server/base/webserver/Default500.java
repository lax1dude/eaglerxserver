package net.lax1dude.eaglercraft.backend.server.base.webserver;

import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

class Default500 extends DefaultHandler {

	protected Default500(EaglerXServer<?> server) {
		super(server);
	}

	@Override
	protected int getCode() {
		return 500;
	}

	@Override
	protected String getContents(EaglerXServer<?> server) {
		return "<h1>HTTP Error 500</h1></h3>(Internal error)</h3>";
	}

}
