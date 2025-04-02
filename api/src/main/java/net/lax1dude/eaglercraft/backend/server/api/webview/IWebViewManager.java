package net.lax1dude.eaglercraft.backend.server.api.webview;

import java.util.Set;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
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

	boolean isDisplayWebViewSupported();

	default void displayWebViewURL(String title, String url) {
		displayWebViewURL(title, url, null);
	}

	void displayWebViewURL(String title, String url, Set<EnumWebViewPerms> permissions);

	default void displayWebViewBlob(String title, IWebViewBlob blob) {
		displayWebViewBlob(title, blob.getHash(), null);
	}

	default void displayWebViewBlob(String title, IWebViewBlob blob, Set<EnumWebViewPerms> permissions) {
		displayWebViewBlob(title, blob.getHash(), permissions);
	}

	default void displayWebViewBlob(String title, SHA1Sum hash) {
		displayWebViewBlob(title, hash, null);
	}

	void displayWebViewBlob(String title, SHA1Sum hash, Set<EnumWebViewPerms> permissions);

}
