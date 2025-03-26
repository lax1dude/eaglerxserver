package net.lax1dude.eaglercraft.backend.server.base.webview;

import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewProvider;

class WebViewDefaultProvider implements IWebViewProvider<Object> {

	private static final WebViewDefaultProvider INSTANCE = new WebViewDefaultProvider();

	@SuppressWarnings("unchecked")
	static <PlayerObject> IWebViewProvider<PlayerObject> instance() {
		return (IWebViewProvider<PlayerObject>) INSTANCE;
	}

	private WebViewDefaultProvider() {
	}

	@Override
	public boolean isChannelAllowed(IWebViewManager<Object> manager) {
		return ((WebViewManager<?>)manager).isChannelAllowedDefault();
	}

	@Override
	public boolean isRequestAllowed(IWebViewManager<Object> manager) {
		return ((WebViewManager<?>)manager).isRequestAllowedDefault();
	}

	@Override
	public void handleRequest(IWebViewManager<Object> manager, SHA1Sum hash, Consumer<IWebViewBlob> callback) {
		((WebViewManager<?>)manager).handleRequestDefault(hash, callback);
	}

}
