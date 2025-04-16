package net.lax1dude.eaglercraft.backend.server.api.webview;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;

public interface IWebViewBlob {

	int getLength();

	@Nonnull
	SHA1Sum getHash();

}
