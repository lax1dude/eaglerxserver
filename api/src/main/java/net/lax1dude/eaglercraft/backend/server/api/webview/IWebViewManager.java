package net.lax1dude.eaglercraft.backend.server.api.webview;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuManager;

public interface IWebViewManager<PlayerObject> {

	@Nonnull
	IEaglerPlayer<PlayerObject> getPlayer();

	@Nonnull
	IWebViewService<PlayerObject> getWebViewService();

	@Nonnull
	default IPauseMenuManager<PlayerObject> getPauseMenuManager() {
		return getPlayer().getPauseMenuManager();
	}

	@Nonnull
	IWebViewProvider<PlayerObject> getProvider();

	void setProvider(@Nonnull IWebViewProvider<PlayerObject> func);

	boolean isRequestAllowed();

	boolean isChannelAllowed();

	boolean isChannelOpen(@Nonnull String channelName);

	@Nonnull
	Set<String> getOpenChannels();

	void sendMessageString(@Nonnull String channelName, @Nonnull String contents);

	void sendMessageString(@Nonnull String channelName, @Nonnull byte[] contents);

	void sendMessageBinary(@Nonnull String channelName, @Nonnull byte[] contents);

	boolean isDisplayWebViewSupported();

	default void displayWebViewURL(@Nonnull String title, @Nonnull String url) {
		displayWebViewURL(title, url, null);
	}

	void displayWebViewURL(@Nonnull String title, @Nonnull String url, @Nullable Set<EnumWebViewPerms> permissions);

	default void displayWebViewBlob(@Nonnull String title, @Nonnull IWebViewBlob blob) {
		displayWebViewBlob(title, blob.getHash(), null);
	}

	default void displayWebViewBlob(@Nonnull String title, @Nonnull IWebViewBlob blob,
			@Nullable Set<EnumWebViewPerms> permissions) {
		displayWebViewBlob(title, blob.getHash(), permissions);
	}

	default void displayWebViewBlob(@Nonnull String title, @Nonnull SHA1Sum hash) {
		displayWebViewBlob(title, hash, null);
	}

	void displayWebViewBlob(@Nonnull String title, @Nonnull SHA1Sum hash, @Nullable Set<EnumWebViewPerms> permissions);

}
