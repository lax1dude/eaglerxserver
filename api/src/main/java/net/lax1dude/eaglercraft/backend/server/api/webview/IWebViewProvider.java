package net.lax1dude.eaglercraft.backend.server.api.webview;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;

public interface IWebViewProvider<PlayerObject> {

	boolean isChannelAllowed(@Nonnull IWebViewManager<PlayerObject> manager);

	boolean isRequestAllowed(@Nonnull IWebViewManager<PlayerObject> manager);

	void handleRequest(@Nonnull IWebViewManager<PlayerObject> manager, @Nonnull SHA1Sum hash,
			@Nonnull Consumer<IWebViewBlob> callback);

	@Nullable
	default SHA1Sum handleAlias(@Nonnull IWebViewManager<PlayerObject> manager,
			@Nonnull String aliasName) {
		return manager.getWebViewService().getBlobFromAlias(aliasName);
	}

}
