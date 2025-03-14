package net.lax1dude.eaglercraft.backend.server.api.webserver;

public interface IPreparedResponse {

	IPreparedResponse retain();

	boolean release();

}
