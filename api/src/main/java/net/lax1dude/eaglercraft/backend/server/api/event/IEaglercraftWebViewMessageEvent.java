package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;

public interface IEaglercraftWebViewMessageEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	public static enum EnumMessageType {
		STRING, BINARY;
	}

	default IWebViewManager<PlayerObject> getWebViewManager() {
		return getPlayer().getWebViewManager();
	}

	default IWebViewService<PlayerObject> getWebViewService() {
		return getServerAPI().getWebViewService();
	}

	EnumMessageType getType();

	String getAsString();

	byte[] getAsBinary();

}
