package net.lax1dude.eaglercraft.backend.server.api.webserver;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;

public interface IPreflightContext extends IRequestContext {

	@Nonnull
	EnumRequestMethod getRequestedMethod();

	@Nonnull
	Iterable<String> getRequestedHeaders();

}
