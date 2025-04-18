package net.lax1dude.eaglercraft.backend.server.api.webview;

import java.io.Closeable;

public interface IWebViewBlobBuilder<Out extends Closeable> extends Closeable {

	Out stream();

	IWebViewBlob build();

}
