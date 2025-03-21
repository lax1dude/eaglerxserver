package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumDiscordInviteButton;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumPauseMenuIcon;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumServerInfoButton;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumWebViewPerms;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public class PauseMenuBuilder implements IPauseMenuBuilder {

	private EnumServerInfoButton serverInfoButtonMode = EnumServerInfoButton.NONE;
	private String serverInfoButtonText = null;
	private String serverInfoURL = null;
	private IWebViewBlob serverInfoButtonBlob = null;
	private String serverInfoTitle = null;
	private Set<EnumWebViewPerms> serverInfoPerms = null;

	private EnumDiscordInviteButton discordButtonMode = EnumDiscordInviteButton.NONE;
	private String discordButtonText = null;
	private String discordButtonURL = null;

	private Map<String, PacketImageData> customIcons;

	@Override
	public IPauseMenuBuilder copyFrom(IPauseMenuBuilder pauseMenu) {
		PauseMenuBuilder builder = (PauseMenuBuilder) pauseMenu;
		serverInfoButtonMode = builder.serverInfoButtonMode;
		serverInfoButtonText = builder.serverInfoButtonText;
		serverInfoURL = builder.serverInfoURL;
		serverInfoButtonBlob = builder.serverInfoButtonBlob;
		serverInfoTitle = builder.serverInfoTitle;
		serverInfoPerms = builder.serverInfoPerms;
		discordButtonMode = builder.discordButtonMode;
		discordButtonText = builder.discordButtonText;
		discordButtonURL = builder.discordButtonURL;
		return this;
	}

	@Override
	public IPauseMenuBuilder copyFrom(ICustomPauseMenu pauseMenu) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumServerInfoButton getServerInfoButtonMode() {
		return serverInfoButtonMode;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeNone() {
		serverInfoButtonMode = EnumServerInfoButton.NONE;
		serverInfoButtonText = null;
		serverInfoURL = null;
		serverInfoButtonBlob = null;
		serverInfoPerms = null;
		serverInfoTitle = null;
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeURL(String text, String url) {
		serverInfoButtonMode = EnumServerInfoButton.EXTERNAL_URL;
		serverInfoButtonText = text;
		serverInfoURL = url;
		serverInfoButtonBlob = null;
		serverInfoPerms = null;
		serverInfoTitle = null;
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeWebViewURL(String text, String title,
			Set<EnumWebViewPerms> permissions, String url) {
		serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_URL;
		serverInfoButtonText = text;
		serverInfoURL = url;
		serverInfoButtonBlob = null;
		serverInfoPerms = permissions;
		serverInfoTitle = title;
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title,
			Set<EnumWebViewPerms> permissions, IWebViewBlob blob) {
		serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_BLOB;
		serverInfoButtonText = text;
		serverInfoURL = null;
		serverInfoButtonBlob = blob;
		serverInfoPerms = permissions;
		serverInfoTitle = title;
		return this;
	}

	@Override
	public String getServerInfoButtonText() {
		return serverInfoButtonText;
	}

	@Override
	public String getServerInfoButtonURL() {
		return serverInfoURL;
	}

	@Override
	public String getServerInfoButtonWebViewTitle() {
		return serverInfoTitle;
	}

	@Override
	public Set<EnumWebViewPerms> getServerInfoButtonWebViewPerms() {
		return serverInfoPerms;
	}

	@Override
	public IWebViewBlob getServerInfoButtonBlob() {
		return serverInfoButtonBlob;
	}

	@Override
	public EnumDiscordInviteButton getDiscordInviteButtonMode() {
		return discordButtonMode;
	}

	@Override
	public IPauseMenuBuilder setDiscordInviteButtonModeNone() {
		discordButtonMode = EnumDiscordInviteButton.NONE;
		discordButtonText = null;
		discordButtonURL = null;
		return this;
	}

	@Override
	public IPauseMenuBuilder setDiscordInviteButtonModeURL(String text, String url) {
		discordButtonMode = EnumDiscordInviteButton.EXTERNAL_URL;
		discordButtonText = text;
		discordButtonURL = url;
		return this;
	}

	@Override
	public String getDiscordInviteButtonText() {
		return discordButtonText;
	}

	@Override
	public String getDiscordInviteButtonURL() {
		return discordButtonURL;
	}

	@Override
	public PacketImageData getMenuIcon(EnumPauseMenuIcon icon) {
		return customIcons != null ? customIcons.get(icon.getIconName()) : null;
	}

	@Override
	public PacketImageData getMenuIcon(String icon) {
		return customIcons != null ? customIcons.get(icon) : null;
	}

	@Override
	public IPauseMenuBuilder setMenuIcon(EnumPauseMenuIcon icon, PacketImageData imageData) {
		if(customIcons == null) {
			customIcons = new HashMap<>();
		}
		customIcons.put(icon.getIconName(), imageData);
		return this;
	}

	@Override
	public IPauseMenuBuilder setMenuIcon(String icon, PacketImageData imageData) {
		if(customIcons == null) {
			customIcons = new HashMap<>();
		}
		customIcons.put(icon, imageData);
		return this;
	}

	@Override
	public ICustomPauseMenu buildPauseMenu() {
		// TODO Auto-generated method stub
		return null;
	}

}
