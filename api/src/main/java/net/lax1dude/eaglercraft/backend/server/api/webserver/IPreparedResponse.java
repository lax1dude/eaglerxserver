package net.lax1dude.eaglercraft.backend.server.api.webserver;

public interface IPreparedResponse {

	void retain();

	boolean release();

}
