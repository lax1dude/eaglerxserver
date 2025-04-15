package net.lax1dude.eaglercraft.backend.server.api.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;

public interface IEaglercraftWebViewMessageEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	public static enum EnumMessageType {
		STRING, BINARY;
	}

	@Nonnull
	default IWebViewManager<PlayerObject> getWebViewManager() {
		return getPlayer().getWebViewManager();
	}

	@Nonnull
	default IWebViewService<PlayerObject> getWebViewService() {
		return getServerAPI().getWebViewService();
	}

	@Nonnull
	String getChannel();

	@Nonnull
	EnumMessageType getType();

	@Nonnull
	String getAsString();

	@Nonnull
	byte[] getAsBinary();

}
