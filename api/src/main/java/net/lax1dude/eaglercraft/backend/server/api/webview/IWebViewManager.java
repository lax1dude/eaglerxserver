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

	boolean isChannelOpen();

	boolean isChannelOpen(String channelName);

	String getChannelName();

	void sendMessageString(String contents);

	void sendMessageString(byte[] contents);

	void sendMessageBinary(byte[] contents);

	void sendMessageString(String channelName, String contents);

	void sendMessageString(String channelName, byte[] contents);

	void sendMessageBinary(String channelName, byte[] contents);

}
