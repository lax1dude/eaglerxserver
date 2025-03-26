package net.lax1dude.eaglercraft.backend.server.api.webview;

import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;

public interface IWebViewBlob {

	int getLength();

	SHA1Sum getHash();

}
