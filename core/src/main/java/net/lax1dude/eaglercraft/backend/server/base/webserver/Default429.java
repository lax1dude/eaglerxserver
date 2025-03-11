package net.lax1dude.eaglercraft.backend.server.base.webserver;

import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

class Default429 extends DefaultHandler {

	protected Default429(EaglerXServer<?> server) {
		super(server);
	}

	@Override
	protected int getCode() {
		return 429;
	}

	@Override
	protected String getContents(EaglerXServer<?> server) {
		return "<h1>HTTP Error 429</h1></h3>(Too many requests!)</h3>";
	}

}
