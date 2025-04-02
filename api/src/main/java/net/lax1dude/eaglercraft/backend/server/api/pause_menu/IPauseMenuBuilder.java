package net.lax1dude.eaglercraft.backend.server.api.pause_menu;

import java.util.Set;

import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public interface IPauseMenuBuilder {

	IPauseMenuBuilder copyFrom(IPauseMenuBuilder pauseMenu);

	IPauseMenuBuilder copyFrom(ICustomPauseMenu pauseMenu);

	EnumServerInfoButton getServerInfoButtonMode();

	IPauseMenuBuilder setServerInfoButtonModeNone();

	IPauseMenuBuilder setServerInfoButtonModeURL(String text, String url);

	IPauseMenuBuilder setServerInfoButtonModeWebViewURL(String text, String title, Set<EnumWebViewPerms> permissions, String url);

	default IPauseMenuBuilder setServerInfoButtonModeWebViewURL(String text, String title, String url) {
		return setServerInfoButtonModeWebViewURL(text, title, null, url);
	}

	IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title, Set<EnumWebViewPerms> permissions, IWebViewBlob blob);

	default IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title, IWebViewBlob blob) {
		return setServerInfoButtonModeWebViewBlob(text, title, null, blob);
	}

	IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title, Set<EnumWebViewPerms> permissions, SHA1Sum hash);

	default IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title, SHA1Sum hash) {
		return setServerInfoButtonModeWebViewBlob(text, title, null, hash);
	}

	String getServerInfoButtonText();

	String getServerInfoButtonURL();

	String getServerInfoButtonWebViewTitle();

	Set<EnumWebViewPerms> getServerInfoButtonWebViewPerms();

	IWebViewBlob getServerInfoButtonBlob();

	SHA1Sum getServerInfoButtonBlobHash();

	EnumDiscordInviteButton getDiscordInviteButtonMode();

	IPauseMenuBuilder setDiscordInviteButtonModeNone();

	IPauseMenuBuilder setDiscordInviteButtonModeURL(String text, String url);

	String getDiscordInviteButtonText();

	String getDiscordInviteButtonURL();

	PacketImageData getMenuIcon(EnumPauseMenuIcon icon);

	PacketImageData getMenuIcon(String icon);

	IPauseMenuBuilder setMenuIcon(EnumPauseMenuIcon icon, PacketImageData imageData);

	IPauseMenuBuilder setMenuIcon(String icon, PacketImageData imageData);

	IPauseMenuBuilder clearMenuIcons();

	ICustomPauseMenu buildPauseMenu();

}
