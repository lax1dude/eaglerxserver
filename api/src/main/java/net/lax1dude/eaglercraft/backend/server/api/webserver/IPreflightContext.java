package net.lax1dude.eaglercraft.backend.server.api.webserver;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;

public interface IPreflightContext extends IRequestContext {

	EnumRequestMethod getRequestedMethod();

	Iterable<String> getRequestedHeaders();

}
