package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;

public interface IEaglercraftWebViewChannelEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	public static enum EnumEventType {
		CHANNEL_OPEN, CHANNEL_CLOSE;
	}

	default IWebViewManager<PlayerObject> getWebViewManager() {
		return getPlayer().getWebViewManager();
	}

	default IWebViewService<PlayerObject> getWebViewService() {
		return getServerAPI().getWebViewService();
	}

	EnumEventType getType();

	String getChannel();

}
