package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;
import net.lax1dude.eaglercraft.backend.rpc.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.EnumDiscordInviteButton;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.EnumPauseMenuIcon;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.EnumServerInfoButton;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.rpc.api.webview.EnumWebViewPerms;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSetPauseMenuCustom;
import net.lax1dude.eaglercraft.backend.rpc.protocol.util.PacketImageData;

public class PauseMenuBuilder implements IPauseMenuBuilder {

	private EnumServerInfoButton serverInfoButtonMode = EnumServerInfoButton.NONE;
	private String serverInfoButtonText = null;
	private String serverInfoURL = null;
	private SHA1Sum serverInfoButtonBlobHash = null;
	private String serverInfoButtonBlobAlias = null;
	private String serverInfoTitle = null;
	private Set<EnumWebViewPerms> serverInfoPerms = null;

	private EnumDiscordInviteButton discordButtonMode = EnumDiscordInviteButton.NONE;
	private String discordButtonText = null;
	private String discordButtonURL = null;

	private static final IPacketImageData INHERIT_ICON = new IPacketImageData() {
		@Override
		public int getWidth() {
			return 0;
		}
		@Override
		public int getHeight() {
			return 0;
		}
		@Override
		public void getPixels(int[] dest, int offset) {
		}
	};

	private Map<String, IPacketImageData> customIcons;

	@Override
	public IPauseMenuBuilder copyFrom(IPauseMenuBuilder pauseMenu) {
		PauseMenuBuilder builder = (PauseMenuBuilder) pauseMenu;
		serverInfoButtonMode = builder.serverInfoButtonMode;
		serverInfoButtonText = builder.serverInfoButtonText;
		serverInfoURL = builder.serverInfoURL;
		serverInfoButtonBlobHash = builder.serverInfoButtonBlobHash;
		serverInfoButtonBlobAlias = builder.serverInfoButtonBlobAlias;
		serverInfoTitle = builder.serverInfoTitle;
		serverInfoPerms = builder.serverInfoPerms != null && !builder.serverInfoPerms.isEmpty()
				? EnumSet.copyOf(builder.serverInfoPerms) : null;
		discordButtonMode = builder.discordButtonMode;
		discordButtonText = builder.discordButtonText;
		discordButtonURL = builder.discordButtonURL;
		customIcons = builder.customIcons != null && !builder.customIcons.isEmpty() ? new HashMap<>(builder.customIcons) : null;
		return this;
	}

	@Override
	public IPauseMenuBuilder copyFrom(ICustomPauseMenu pauseMenu) {
		CPacketRPCSetPauseMenuCustom pkt = CustomPauseMenuWrapper.unwrap(pauseMenu);
		switch(pkt.serverInfoMode) {
		case CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_NONE:
		default:
			serverInfoButtonMode = EnumServerInfoButton.NONE;
			serverInfoButtonText = null;
			serverInfoURL = null;
			serverInfoButtonBlobHash = null;
			serverInfoButtonBlobAlias = null;
			serverInfoTitle = null;
			serverInfoPerms = null;
			break;
		case CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_EXTERNAL_URL:
			serverInfoButtonMode = EnumServerInfoButton.EXTERNAL_URL;
			serverInfoButtonText = pkt.serverInfoButtonText;
			serverInfoURL = pkt.serverInfoURL;
			serverInfoButtonBlobHash = null;
			serverInfoButtonBlobAlias = null;
			serverInfoTitle = null;
			serverInfoPerms = null;
			break;
		case CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP:
			serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_URL;
			serverInfoButtonText = pkt.serverInfoButtonText;
			serverInfoURL = pkt.serverInfoURL;
			serverInfoButtonBlobHash = null;
			serverInfoButtonBlobAlias = null;
			serverInfoTitle = pkt.serverInfoEmbedTitle;
			serverInfoPerms = pkt.serverInfoEmbedPerms != 0 ? EnumWebViewPerms.fromBits(pkt.serverInfoEmbedPerms) : null;
			break;
		case CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_SHOW_EMBED_OVER_WS:
			serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_URL;
			serverInfoButtonText = pkt.serverInfoButtonText;
			serverInfoURL = null;
			serverInfoButtonBlobHash = SHA1Sum.create(pkt.serverInfoHash);
			serverInfoButtonBlobAlias = null;
			serverInfoTitle = pkt.serverInfoEmbedTitle;
			serverInfoPerms = pkt.serverInfoEmbedPerms != 0 ? EnumWebViewPerms.fromBits(pkt.serverInfoEmbedPerms) : null;
			break;
		case CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_SHOW_NAMED_EMBED_OVER_WS:
			serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_ALIAS;
			serverInfoButtonText = pkt.serverInfoButtonText;
			serverInfoURL = null;
			serverInfoButtonBlobHash = null;
			serverInfoButtonBlobAlias = pkt.serverInfoURL;
			serverInfoTitle = pkt.serverInfoEmbedTitle;
			serverInfoPerms = pkt.serverInfoEmbedPerms != 0 ? EnumWebViewPerms.fromBits(pkt.serverInfoEmbedPerms) : null;
			break;
		case CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_INHERIT_DEFAULT:
			serverInfoButtonMode = EnumServerInfoButton.INHERIT_DEFAULT;
			serverInfoButtonText = null;
			serverInfoURL = null;
			serverInfoButtonBlobHash = null;
			serverInfoButtonBlobAlias = null;
			serverInfoTitle = null;
			serverInfoPerms = null;
			break;
		}
		switch(pkt.discordButtonMode) {
		case CPacketRPCSetPauseMenuCustom.DISCORD_MODE_NONE:
		default:
			discordButtonMode = EnumDiscordInviteButton.NONE;
			discordButtonText = null;
			discordButtonURL = null;
			break;
		case CPacketRPCSetPauseMenuCustom.DISCORD_MODE_INVITE_URL:
			discordButtonMode = EnumDiscordInviteButton.EXTERNAL_URL;
			discordButtonText = pkt.discordButtonText;
			discordButtonURL = pkt.discordInviteURL;
			break;
		case CPacketRPCSetPauseMenuCustom.DISCORD_MODE_INHERIT_DEFAULT:
			discordButtonMode = EnumDiscordInviteButton.INHERIT_DEFAULT;
			discordButtonText = null;
			discordButtonURL = null;
			break;
		}
		if(pkt.imageMappings != null && !pkt.imageMappings.isEmpty()) {
			customIcons = new HashMap<>();
			for(Entry<String, Integer> etr : pkt.imageMappings.entrySet()) {
				int i = etr.getValue();
				if(i == CPacketRPCSetPauseMenuCustom.ICON_ID_INHERIT) {
					customIcons.put(etr.getKey(), INHERIT_ICON);
				}else if(i >= 0 && i < pkt.imageData.size()) {
					customIcons.put(etr.getKey(), PacketImageDataWrapper.wrap(pkt.imageData.get(i)));
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
		serverInfoButtonBlobHash = null;
		serverInfoButtonBlobAlias = null;
		serverInfoPerms = null;
		serverInfoTitle = null;
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeURL(String text, String url) {
		serverInfoButtonMode = EnumServerInfoButton.EXTERNAL_URL;
		serverInfoButtonText = text;
		serverInfoURL = url;
		serverInfoButtonBlobHash = null;
		serverInfoButtonBlobAlias = null;
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
		serverInfoButtonBlobHash = null;
		serverInfoButtonBlobAlias = null;
		serverInfoPerms = permissions;
		serverInfoTitle = title;
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title,
			Set<EnumWebViewPerms> permissions, SHA1Sum hash) {
		serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_BLOB;
		serverInfoButtonText = text;
		serverInfoURL = null;
		serverInfoButtonBlobHash = hash;
		serverInfoButtonBlobAlias = null;
		serverInfoPerms = permissions;
		serverInfoTitle = title;
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title,
			Set<EnumWebViewPerms> permissions, String blobAlias) {
		serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_ALIAS;
		serverInfoButtonText = text;
		serverInfoURL = null;
		serverInfoButtonBlobHash = null;
		serverInfoButtonBlobAlias = blobAlias;
		serverInfoPerms = permissions;
		serverInfoTitle = title;
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeInheritDefault() {
		serverInfoButtonMode = EnumServerInfoButton.INHERIT_DEFAULT;
		serverInfoButtonText = null;
		serverInfoURL = null;
		serverInfoButtonBlobHash = null;
		serverInfoButtonBlobAlias = null;
		serverInfoPerms = null;
		serverInfoTitle = null;
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
		return serverInfoPerms != null ? serverInfoPerms : Collections.emptySet();
	}

	@Override
	public SHA1Sum getServerInfoButtonBlobHash() {
		return serverInfoButtonBlobHash;
	}

	@Override
	public String getServerInfoButtonBlobAlias() {
		return serverInfoButtonBlobAlias;
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
	public IPauseMenuBuilder setDiscordInviteButtonModeInheritDefault() {
		discordButtonMode = EnumDiscordInviteButton.INHERIT_DEFAULT;
		discordButtonText = null;
		discordButtonURL = null;
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
	public IPacketImageData getMenuIcon(EnumPauseMenuIcon icon) {
		if(customIcons == null) {
			return null;
		}
		IPacketImageData ret = customIcons.get(icon.getIconName());
		return ret != INHERIT_ICON ? ret : null;
	}

	@Override
	public IPacketImageData getMenuIcon(String icon) {
		if(customIcons == null) {
			return null;
		}
		IPacketImageData ret = customIcons.get(icon);
		return ret != INHERIT_ICON ? ret : null;
	}

	@Override
	public IPauseMenuBuilder setMenuIcon(EnumPauseMenuIcon icon, IPacketImageData imageData) {
		checkDimensions(imageData);
		if(customIcons == null) {
			customIcons = new HashMap<>();
		}
		customIcons.put(icon.getIconName(), imageData);
		return this;
	}

	@Override
	public IPauseMenuBuilder setMenuIcon(String icon, IPacketImageData imageData) {
		checkDimensions(imageData);
		if(customIcons == null) {
			customIcons = new HashMap<>();
		}
		customIcons.put(icon, imageData);
		return this;
	}

	@Override
	public IPauseMenuBuilder setMenuIconInheritDefault(String icon) {
		if(customIcons == null) {
			customIcons = new HashMap<>();
		}
		customIcons.put(icon, INHERIT_ICON);
		return this;
	}

	@Override
	public boolean isMenuIconInheritDefault(String icon) {
		return customIcons != null && customIcons.get(icon) == INHERIT_ICON;
	}

	@Override
	public boolean isMenuIconInheritDefault(EnumPauseMenuIcon icon) {
		return customIcons != null && customIcons.get(icon.getIconName()) == INHERIT_ICON;
	}

	@Override
	public IPauseMenuBuilder clearMenuIcons() {
		customIcons = null;
		return this;
	}

	private static void checkDimensions(IPacketImageData etr) {
		if(etr.getWidth() < 1 || etr.getWidth() > 64 || etr.getHeight() < 1 || etr.getHeight() > 64) {
			throw new IllegalArgumentException("Invalid image dimensions, must be between 1x1 and 64x64, got " + etr.getWidth() + "x" + etr.getHeight());
		}
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
			serverInfoMode = CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_NONE;
			serverInfoButtonText = null;
			serverInfoURL = null;
			serverInfoHash = null;
			serverInfoEmbedPerms = 0;
			serverInfoEmbedTitle = null;
			break;
		case EXTERNAL_URL:
			serverInfoMode = CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_EXTERNAL_URL;
			serverInfoButtonText = this.serverInfoButtonText;
			serverInfoURL = this.serverInfoURL;
			serverInfoHash = null;
			serverInfoEmbedPerms = 0;
			serverInfoEmbedTitle = null;
			break;
		case WEBVIEW_URL:
			serverInfoMode = CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP;
			serverInfoButtonText = this.serverInfoButtonText;
			serverInfoURL = this.serverInfoURL;
			serverInfoHash = null;
			serverInfoEmbedPerms = serverInfoPerms != null ? EnumWebViewPerms.toBits(serverInfoPerms) : 0;
			serverInfoEmbedTitle = serverInfoTitle;
			break;
		case WEBVIEW_BLOB:
			serverInfoMode = CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_SHOW_EMBED_OVER_WS;
			serverInfoButtonText = this.serverInfoButtonText;
			serverInfoURL = null;
			serverInfoHash = serverInfoButtonBlobHash.asBytes();
			serverInfoEmbedPerms = serverInfoPerms != null ? EnumWebViewPerms.toBits(serverInfoPerms) : 0;
			serverInfoEmbedTitle = serverInfoTitle;
			break;
		case WEBVIEW_ALIAS:
			serverInfoMode = CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_SHOW_EMBED_OVER_WS;
			serverInfoButtonText = this.serverInfoButtonText;
			serverInfoURL = serverInfoButtonBlobAlias;
			serverInfoHash = null;
			serverInfoEmbedPerms = serverInfoPerms != null ? EnumWebViewPerms.toBits(serverInfoPerms) : 0;
			serverInfoEmbedTitle = serverInfoTitle;
			break;
		case INHERIT_DEFAULT:
			serverInfoMode = CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_INHERIT_DEFAULT;
			serverInfoButtonText = null;
			serverInfoURL = null;
			serverInfoHash = null;
			serverInfoEmbedPerms = 0;
			serverInfoEmbedTitle = null;
			break;
		}
		int discordButtonMode;
		String discordButtonText;
		String discordInviteURL;
		switch(this.discordButtonMode) {
		case NONE:
		default:
			discordButtonMode = CPacketRPCSetPauseMenuCustom.DISCORD_MODE_NONE;
			discordButtonText = null;
			discordInviteURL = null;
			break;
		case EXTERNAL_URL:
			discordButtonMode = CPacketRPCSetPauseMenuCustom.DISCORD_MODE_INVITE_URL;
			discordButtonText = this.discordButtonText;
			discordInviteURL = discordButtonURL;
			break;
		case INHERIT_DEFAULT:
			discordButtonMode = CPacketRPCSetPauseMenuCustom.DISCORD_MODE_INHERIT_DEFAULT;
			discordButtonText = null;
			discordInviteURL = null;
			break;
		}
		Map<String, Integer> imageMappings;
		List<PacketImageData> imageData;
		if(customIcons != null && !customIcons.isEmpty()) {
			imageMappings = new HashMap<>(customIcons.size());
			Map<IPacketImageData, Integer> imageDataMap = new HashMap<>(customIcons.size());
			for(Map.Entry<String, IPacketImageData> etr : customIcons.entrySet()) {
				IPacketImageData imgData = etr.getValue();
				if(imgData == INHERIT_ICON) {
					imageMappings.put(etr.getKey(), CPacketRPCSetPauseMenuCustom.ICON_ID_INHERIT);
				}else {
					int sz = imageDataMap.size();
					Integer i = imageDataMap.putIfAbsent(imgData, sz);
					if(i != null) {
						imageMappings.put(etr.getKey(), i);
					}else {
						imageMappings.put(etr.getKey(), sz);
					}
				}
			}
			PacketImageData[] imageDataArr = new PacketImageData[imageDataMap.size()];
			for(Map.Entry<IPacketImageData, Integer> etr : imageDataMap.entrySet()) {
				imageDataArr[etr.getValue()] = PacketImageDataWrapper.unwrap(etr.getKey());
			}
			imageData = Arrays.asList(imageDataArr);
		}else {
			imageMappings = null;
			imageData = null;
		}
		return CustomPauseMenuWrapper.wrap(new CPacketRPCSetPauseMenuCustom(serverInfoMode, serverInfoButtonText,
				serverInfoURL, serverInfoHash, serverInfoEmbedPerms, serverInfoEmbedTitle, discordButtonMode,
				discordButtonText, discordInviteURL, imageMappings, imageData));
	}

	@Override
	public boolean isRemoteFeaturesSupported() {
		return true;
	}

}
