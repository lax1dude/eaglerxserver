package net.lax1dude.eaglercraft.backend.rpc.api.pause_menu;

import java.util.Set;

import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;
import net.lax1dude.eaglercraft.backend.rpc.api.SHA1Sum;

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

	String getServerInfoButtonText();

	String getServerInfoButtonURL();

	String getServerInfoButtonWebViewTitle();

	Set<EnumWebViewPerms> getServerInfoButtonWebViewPerms();

	SHA1Sum getServerInfoButtonBlobHash();

	EnumDiscordInviteButton getDiscordInviteButtonMode();

	IPauseMenuBuilder setDiscordInviteButtonModeNone();

	IPauseMenuBuilder setDiscordInviteButtonModeURL(String text, String url);

	String getDiscordInviteButtonText();

	String getDiscordInviteButtonURL();

	IPacketImageData getMenuIcon(EnumPauseMenuIcon icon);

	IPacketImageData getMenuIcon(String icon);

	IPauseMenuBuilder setMenuIcon(EnumPauseMenuIcon icon, IPacketImageData imageData);

	IPauseMenuBuilder setMenuIcon(String icon, IPacketImageData imageData);

	IPauseMenuBuilder clearMenuIcons();

	ICustomPauseMenu buildPauseMenu();

}
