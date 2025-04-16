package net.lax1dude.eaglercraft.backend.server.api.webserver;

import javax.annotation.Nonnull;

public interface IPreparedResponse {

	@Nonnull
	IPreparedResponse retain();

	boolean release();

}
