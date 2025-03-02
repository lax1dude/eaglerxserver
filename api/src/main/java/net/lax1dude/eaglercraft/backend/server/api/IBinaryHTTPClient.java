package net.lax1dude.eaglercraft.backend.server.api;

import java.net.URI;
import java.util.function.Consumer;

public interface IBinaryHTTPClient {

	void asyncRequest(EnumRequestMethod method, URI requestURI, Consumer<IBinaryHTTPResponse> responseCallback);

}
