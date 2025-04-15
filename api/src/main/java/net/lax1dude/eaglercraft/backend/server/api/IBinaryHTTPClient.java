package net.lax1dude.eaglercraft.backend.server.api;

import java.net.URI;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

public interface IBinaryHTTPClient {

	void asyncRequest(@Nonnull EnumRequestMethod method, @Nonnull URI requestURI,
			@Nonnull Consumer<IBinaryHTTPResponse> responseCallback);

}
