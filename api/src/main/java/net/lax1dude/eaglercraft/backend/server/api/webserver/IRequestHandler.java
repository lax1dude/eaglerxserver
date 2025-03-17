package net.lax1dude.eaglercraft.backend.server.api.webserver;

public interface IRequestHandler {

	default void bind(IWebServer server) {
	}

	default void unbind(IWebServer server) {
	}

	void handleRequest(IRequestContext requestContext);

	default boolean enablePreflight() {
		return false;
	}

	default void handlePreflight(IPreflightContext requestContext) {
	}

}
