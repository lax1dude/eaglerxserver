package net.lax1dude.eaglercraft.backend.server.api.webview;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuManager;

public interface IWebViewManager<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IWebViewService<PlayerObject> getWebViewService();

	IPauseMenuManager<PlayerObject> getPauseMenuManager();

	EnumWebViewState getWebViewState();

	boolean isWebViewSupported();

	boolean isWebViewAllowed();

	boolean isWebViewChannelOpen();

	boolean isWebViewChannelOpen(String channelName);

	String getWebViewChannelName();

	void sendWebViewMessage(String contents);

	void sendWebViewMessage(byte[] contents);

	void sendWebViewMessage(String channelName, String contents);

	void sendWebViewMessage(String channelName, byte[] contents);

}
