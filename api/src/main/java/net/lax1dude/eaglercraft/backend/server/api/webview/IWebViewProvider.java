package net.lax1dude.eaglercraft.backend.server.api.webview;

import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;

public interface IWebViewProvider<PlayerObject> {

	boolean isChannelAllowed(IWebViewManager<PlayerObject> manager);

	boolean isRequestAllowed(IWebViewManager<PlayerObject> manager);

	void handleRequest(IWebViewManager<PlayerObject> manager, SHA1Sum hash, Consumer<IWebViewBlob> callback);

}
