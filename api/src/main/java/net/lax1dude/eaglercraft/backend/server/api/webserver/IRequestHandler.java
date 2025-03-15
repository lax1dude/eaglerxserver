package net.lax1dude.eaglercraft.backend.server.api.webserver;

import net.lax1dude.eaglercraft.backend.server.api.EnumRequestMethod;

public interface IRequestHandler {

	default void bind(IWebServer server) {
	}

	default void unbind(IWebServer server) {
	}

	default boolean isEnableCORS() {
		return false;
	}

	default boolean handleCORSAllowOrigin(String origin, EnumRequestMethod method, String path, String query) {
		return true;
	}

	void handleRequest(IRequestContext requestContext);

}
