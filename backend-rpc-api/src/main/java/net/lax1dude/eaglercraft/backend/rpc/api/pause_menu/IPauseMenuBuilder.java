package net.lax1dude.eaglercraft.backend.rpc.api.pause_menu;

import java.util.Set;

import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;
import net.lax1dude.eaglercraft.backend.rpc.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.rpc.api.webview.EnumWebViewPerms;

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

	IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title, Set<EnumWebViewPerms> permissions, SHA1Sum blobHash);

	default IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title, SHA1Sum blobHash) {
		return setServerInfoButtonModeWebViewBlob(text, title, null, blobHash);
	}

	IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title, Set<EnumWebViewPerms> permissions, String blobAlias);

	default IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title, String blobAlias) {
		return setServerInfoButtonModeWebViewBlob(text, title, null, blobAlias);
	}

	IPauseMenuBuilder setServerInfoButtonModeInheritDefault();

	String getServerInfoButtonText();

	String getServerInfoButtonURL();

	String getServerInfoButtonWebViewTitle();

	Set<EnumWebViewPerms> getServerInfoButtonWebViewPerms();

	SHA1Sum getServerInfoButtonBlobHash();

	EnumDiscordInviteButton getDiscordInviteButtonMode();

	IPauseMenuBuilder setDiscordInviteButtonModeNone();

	IPauseMenuBuilder setDiscordInviteButtonModeURL(String text, String url);

	IPauseMenuBuilder setDiscordInviteButtonModeInheritDefault();

	String getDiscordInviteButtonText();

	String getDiscordInviteButtonURL();

	boolean isMenuIconInheritDefault(EnumPauseMenuIcon icon);

	boolean isMenuIconInheritDefault(String icon);

	IPacketImageData getMenuIcon(EnumPauseMenuIcon icon);

	IPacketImageData getMenuIcon(String icon);

	IPauseMenuBuilder setMenuIcon(EnumPauseMenuIcon icon, IPacketImageData imageData);

	IPauseMenuBuilder setMenuIcon(String icon, IPacketImageData imageData);

	IPauseMenuBuilder setMenuIconInheritDefault(String icon);

	IPauseMenuBuilder clearMenuIcons();

	boolean isRemoteFeaturesSupported();

	ICustomPauseMenu buildPauseMenu();

}
