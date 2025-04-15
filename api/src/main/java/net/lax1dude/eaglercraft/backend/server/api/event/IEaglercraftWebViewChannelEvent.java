package net.lax1dude.eaglercraft.backend.server.api.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;

public interface IEaglercraftWebViewChannelEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	public static enum EnumEventType {
		CHANNEL_OPEN, CHANNEL_CLOSE;
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
	EnumEventType getType();

	@Nonnull
	String getChannel();

}
