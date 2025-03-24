package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;

import net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumDiscordInviteButton;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumPauseMenuIcon;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumServerInfoButton;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumWebViewPerms;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketCustomizePauseMenuV4EAG;
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
		IPauseMenuImpl impl = (IPauseMenuImpl) pauseMenu;
		SPacketCustomizePauseMenuV4EAG pkt = impl.getPacket();
		switch(pkt.serverInfoMode) {
		case SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_NONE:
		default:
			serverInfoButtonMode = EnumServerInfoButton.NONE;
			serverInfoButtonText = null;
			serverInfoURL = null;
			serverInfoButtonBlob = null;
			serverInfoTitle = null;
			serverInfoPerms = null;
			break;
		case SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_EXTERNAL_URL:
			serverInfoButtonMode = EnumServerInfoButton.EXTERNAL_URL;
			serverInfoButtonText = pkt.serverInfoButtonText;
			serverInfoURL = pkt.serverInfoURL;
			serverInfoButtonBlob = null;
			serverInfoTitle = null;
			serverInfoPerms = null;
			break;
		case SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP:
			serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_URL;
			serverInfoButtonText = pkt.serverInfoButtonText;
			serverInfoURL = pkt.serverInfoURL;
			serverInfoButtonBlob = null;
			serverInfoTitle = pkt.serverInfoEmbedTitle;
			serverInfoPerms = EnumWebViewPerms.fromBits(pkt.serverInfoEmbedPerms);
			break;
		case SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_SHOW_EMBED_OVER_WS:
			serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_URL;
			serverInfoButtonText = pkt.serverInfoButtonText;
			serverInfoURL = null;
			serverInfoButtonBlob = impl.getBlob();
			serverInfoTitle = pkt.serverInfoEmbedTitle;
			serverInfoPerms = EnumWebViewPerms.fromBits(pkt.serverInfoEmbedPerms);
			break;
		}
		switch(pkt.discordButtonMode) {
		case SPacketCustomizePauseMenuV4EAG.DISCORD_MODE_NONE:
		default:
			discordButtonMode = EnumDiscordInviteButton.NONE;
			discordButtonText = null;
			discordButtonURL = null;
			break;
		case SPacketCustomizePauseMenuV4EAG.DISCORD_MODE_INVITE_URL:
			discordButtonMode = EnumDiscordInviteButton.EXTERNAL_URL;
			discordButtonText = pkt.discordButtonText;
			discordButtonURL = pkt.discordInviteURL;
			break;
		}
		if(pkt.imageMappings != null && !pkt.imageMappings.isEmpty()) {
			customIcons = new HashMap<>();
			for(Entry<String, Integer> etr : pkt.imageMappings.entrySet()) {
				int i = etr.getValue();
				if(i >= 0 && i < pkt.imageData.size()) {
					customIcons.put(etr.getKey(), pkt.imageData.get(i));
				}
			}
		}else {
			customIcons = null;
		}
		return this;
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
		int serverInfoMode;
		String serverInfoButtonText;
		String serverInfoURL;
		byte[] serverInfoHash;
		int serverInfoEmbedPerms;
		String serverInfoEmbedTitle;
		switch(serverInfoButtonMode) {
		case NONE:
		default:
			
			break;
		case EXTERNAL_URL:
			
			break;
		case WEBVIEW_URL:
			
			break;
		case WEBVIEW_BLOB:
			
			break;
		}
		int discordButtonMode;
		String discordButtonText;
		String discordInviteURL;
		switch(this.discordButtonMode) {
		case NONE:
		default:
			discordButtonMode = SPacketCustomizePauseMenuV4EAG.DISCORD_MODE_NONE;
			discordButtonText = null;
			discordInviteURL = null;
			break;
		case EXTERNAL_URL:
			discordButtonMode = SPacketCustomizePauseMenuV4EAG.DISCORD_MODE_INVITE_URL;
			discordButtonText = this.discordButtonText;
			discordInviteURL = discordButtonURL;
			break;
		}
		Map<String,Integer> imageMappings;
		List<PacketImageData> imageData;
		if(customIcons != null && !customIcons.isEmpty()) {
			//TODO
		}else {
			imageMappings = null;
			imageData = null;
		}
		return null;
	}

}
