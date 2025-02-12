package net.lax1dude.eaglercraft.backend.server.api.pause_menu;

import java.util.Set;

import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public interface IPauseMenuBuilder {

	IPauseMenuBuilder copyFrom(ICustomPauseMenu pauseMenu);

	EnumServerInfoButton getServerInfoButtonMode();

	IPauseMenuBuilder setServerInfoButtonModeNone();

	IPauseMenuBuilder setServerInfoButtonModeURL(String text, String url);

	IPauseMenuBuilder setServerInfoButtonModeWebViewURL(String text, String title, Set<EnumWebViewPerms> permissions, String url);

	IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title, Set<EnumWebViewPerms> permissions, IWebViewBlob blob);

	String getServerInfoButtonText();

	String getServerInfoButtonURL();

	String getServerInfoButtonWebViewTitle();

	Set<EnumWebViewPerms> getServerInfoButtonWebViewPerms();

	IWebViewBlob getServerInfoButtonBlob();

	EnumDiscordInviteButton getDiscordInviteButtonMode();

	IPauseMenuBuilder setDiscordInviteButtonModeNone();

	IPauseMenuBuilder setDiscordInviteButtonModeURL(String text, String url);

	String getDiscordInviteButtonText();

	String getDiscordInviteButtonURL();

	PacketImageData getMenuIcon(EnumPauseMenuIcon icon);

	IPauseMenuBuilder setMenuIcon(EnumPauseMenuIcon icon, PacketImageData imageData);

	ICustomPauseMenu buildPauseMenu();

}
