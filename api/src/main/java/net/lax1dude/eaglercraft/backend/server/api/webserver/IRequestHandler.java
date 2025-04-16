package net.lax1dude.eaglercraft.backend.server.api.webserver;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface IRequestHandler {

	default void bind(@Nonnull IWebServer server) {
	}

	default void unbind(@Nonnull IWebServer server) {
	}

	void handleRequest(@Nonnull IRequestContext requestContext);

	default boolean enablePreflight() {
		return false;
	}

	default void handlePreflight(@Nonnull IPreflightContext requestContext) {
	}

}
