package net.lax1dude.eaglercraft.backend.rpc.api.pause_menu;

import java.util.Set;

import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;

public interface IPauseMenuBuilder {

	IPauseMenuBuilder copyFrom(IPauseMenuBuilder pauseMenu);

	IPauseMenuBuilder copyFrom(ICustomPauseMenu pauseMenu);

	EnumServerInfoButton getServerInfoButtonMode();

	IPauseMenuBuilder setServerInfoButtonModeNone();

	IPauseMenuBuilder setServerInfoButtonModeURL(String text, String url);

	IPauseMenuBuilder setServerInfoButtonModeWebViewURL(String text, String title, Set<EnumWebViewPerms> permissions, String url);

	IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title, Set<EnumWebViewPerms> permissions, byte[] blobHash);

	String getServerInfoButtonText();

	String getServerInfoButtonURL();

	String getServerInfoButtonWebViewTitle();

	Set<EnumWebViewPerms> getServerInfoButtonWebViewPerms();

	byte[] getServerInfoButtonBlobHash();

	EnumDiscordInviteButton getDiscordInviteButtonMode();

	IPauseMenuBuilder setDiscordInviteButtonModeNone();

	IPauseMenuBuilder setDiscordInviteButtonModeURL(String text, String url);

	String getDiscordInviteButtonText();

	String getDiscordInviteButtonURL();

	IPacketImageData getMenuIcon(EnumPauseMenuIcon icon);

	IPacketImageData getMenuIcon(String icon);

	IPauseMenuBuilder setMenuIcon(EnumPauseMenuIcon icon, IPacketImageData imageData);

	IPauseMenuBuilder setMenuIcon(String icon, IPacketImageData imageData);

	ICustomPauseMenu buildPauseMenu();

}
