package net.lax1dude.eaglercraft.backend.server.api.webview;

import java.util.Set;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuManager;

public interface IWebViewManager<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IWebViewService<PlayerObject> getWebViewService();

	default IPauseMenuManager<PlayerObject> getPauseMenuManager() {
		return getPlayer().getPauseMenuManager();
	}

	IWebViewProvider<PlayerObject> getProvider();

	void setProvider(IWebViewProvider<PlayerObject> func);

	boolean isRequestAllowed();

	boolean isChannelAllowed();

	boolean isChannelOpen(String channelName);

	Set<String> getOpenChannels();

	void sendMessageString(String channelName, String contents);

	void sendMessageString(String channelName, byte[] contents);

	void sendMessageBinary(String channelName, byte[] contents);

}
