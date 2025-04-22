/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumDiscordInviteButton;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumPauseMenuIcon;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumServerInfoButton;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuBuilder;
import net.lax1dude.eaglercraft.backend.server.api.webview.EnumWebViewPerms;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketCustomizePauseMenuV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public class PauseMenuBuilder implements IPauseMenuBuilder {

	private EnumServerInfoButton serverInfoButtonMode = EnumServerInfoButton.NONE;
	private String serverInfoButtonText = null;
	private String serverInfoURL = null;
	private IWebViewBlob serverInfoButtonBlob = null;
	private SHA1Sum serverInfoButtonBlobHash = null;
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
		serverInfoButtonBlobHash = builder.serverInfoButtonBlobHash;
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
		IPauseMenuImpl impl = (IPauseMenuImpl) pauseMenu;
		SPacketCustomizePauseMenuV4EAG pkt = impl.getPacket();
		switch(pkt.serverInfoMode) {
		case SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_NONE:
		default:
			serverInfoButtonMode = EnumServerInfoButton.NONE;
			serverInfoButtonText = null;
			serverInfoURL = null;
			serverInfoButtonBlob = null;
			serverInfoButtonBlobHash = null;
			serverInfoTitle = null;
			serverInfoPerms = null;
			break;
		case SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_EXTERNAL_URL:
			serverInfoButtonMode = EnumServerInfoButton.EXTERNAL_URL;
			serverInfoButtonText = pkt.serverInfoButtonText;
			serverInfoURL = pkt.serverInfoURL;
			serverInfoButtonBlob = null;
			serverInfoButtonBlobHash = null;
			serverInfoTitle = null;
			serverInfoPerms = null;
			break;
		case SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP:
			serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_URL;
			serverInfoButtonText = pkt.serverInfoButtonText;
			serverInfoURL = pkt.serverInfoURL;
			serverInfoButtonBlob = null;
			serverInfoButtonBlobHash = null;
			serverInfoTitle = pkt.serverInfoEmbedTitle;
			serverInfoPerms = pkt.serverInfoEmbedPerms != 0 ? EnumWebViewPerms.fromBits(pkt.serverInfoEmbedPerms) : null;
			break;
		case SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_SHOW_EMBED_OVER_WS:
			serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_URL;
			serverInfoButtonText = pkt.serverInfoButtonText;
			serverInfoURL = null;
			serverInfoButtonBlob = impl.getBlob();
			serverInfoButtonBlobHash = SHA1Sum.create(pkt.serverInfoHash);
			if(serverInfoButtonBlob != null) {
				SHA1Sum sum = serverInfoButtonBlob.getHash();
				if(sum.equals(serverInfoButtonBlobHash)) {
					serverInfoButtonBlobHash = sum;
				}
			}
			serverInfoTitle = pkt.serverInfoEmbedTitle;
			serverInfoPerms = pkt.serverInfoEmbedPerms != 0 ? EnumWebViewPerms.fromBits(pkt.serverInfoEmbedPerms) : null;
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
		serverInfoButtonBlobHash = null;
		serverInfoPerms = null;
		serverInfoTitle = null;
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeURL(String text, String url) {
		if(text == null) {
			throw new NullPointerException("text");
		}
		if(url == null) {
			throw new NullPointerException("url");
		}
		serverInfoButtonMode = EnumServerInfoButton.EXTERNAL_URL;
		serverInfoButtonText = text;
		serverInfoURL = url;
		serverInfoButtonBlob = null;
		serverInfoButtonBlobHash = null;
		serverInfoPerms = null;
		serverInfoTitle = null;
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeWebViewURL(String text, String title,
			Set<EnumWebViewPerms> permissions, String url) {
		if(text == null) {
			throw new NullPointerException("text");
		}
		if(title == null) {
			throw new NullPointerException("title");
		}
		if(url == null) {
			throw new NullPointerException("url");
		}
		serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_URL;
		serverInfoButtonText = text;
		serverInfoURL = url;
		serverInfoButtonBlob = null;
		serverInfoButtonBlobHash = null;
		serverInfoPerms = permissions;
		serverInfoTitle = title;
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title,
			Set<EnumWebViewPerms> permissions, IWebViewBlob blob) {
		if(text == null) {
			throw new NullPointerException("text");
		}
		if(title == null) {
			throw new NullPointerException("title");
		}
		if(blob == null) {
			throw new NullPointerException("blob");
		}
		serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_BLOB;
		serverInfoButtonText = text;
		serverInfoURL = null;
		serverInfoButtonBlob = blob;
		serverInfoButtonBlobHash = blob.getHash();
		serverInfoPerms = permissions;
		serverInfoTitle = title;
		return this;
	}

	@Override
	public IPauseMenuBuilder setServerInfoButtonModeWebViewBlob(String text, String title,
			Set<EnumWebViewPerms> permissions, SHA1Sum hash) {
		if(text == null) {
			throw new NullPointerException("text");
		}
		if(title == null) {
			throw new NullPointerException("title");
		}
		if(hash == null) {
			throw new NullPointerException("hash");
		}
		serverInfoButtonMode = EnumServerInfoButton.WEBVIEW_BLOB;
		serverInfoButtonText = text;
		serverInfoURL = null;
		serverInfoButtonBlob = null;
		serverInfoButtonBlobHash = hash;
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
		return serverInfoPerms != null ? serverInfoPerms : Collections.emptySet();
	}

	@Override
	public IWebViewBlob getServerInfoButtonBlob() {
		return serverInfoButtonBlob;
	}

	@Override
	public SHA1Sum getServerInfoButtonBlobHash() {
		return serverInfoButtonBlobHash;
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
		if(text == null) {
			throw new NullPointerException("text");
		}
		if(url == null) {
			throw new NullPointerException("url");
		}
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
		if(icon == null) {
			throw new NullPointerException("icon");
		}
		return customIcons != null ? customIcons.get(icon) : null;
	}

	@Override
	public IPauseMenuBuilder setMenuIcon(EnumPauseMenuIcon icon, PacketImageData imageData) {
		if(icon == null) {
			throw new NullPointerException("icon");
		}
		if(imageData != null) {
			checkDimensions(imageData);
			if(customIcons == null) {
				customIcons = new HashMap<>();
			}
			customIcons.put(icon.getIconName(), imageData);
		}else if(customIcons != null) {
			customIcons.remove(icon.getIconName());
		}
		return this;
	}

	@Override
	public IPauseMenuBuilder setMenuIcon(String icon, PacketImageData imageData) {
		if(icon == null) {
			throw new NullPointerException("icon");
		}
		if(imageData != null) {
			checkDimensions(imageData);
			if(customIcons == null) {
				customIcons = new HashMap<>();
			}
			customIcons.put(icon, imageData);
		}else if(customIcons != null) {
			customIcons.remove(icon);
		}
		return this;
	}

	@Override
	public IPauseMenuBuilder clearMenuIcons() {
		customIcons = null;
		return this;
	}

	private static void checkDimensions(PacketImageData etr) {
		if(etr.width < 1 || etr.width > 64 || etr.height < 1 || etr.height > 64) {
			throw new IllegalArgumentException("Invalid image dimensions, must be between 1x1 and 64x64, got " + etr.width + "x" + etr.height);
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
		IWebViewBlob serverInfoBlob;
		boolean permitChannel;
		switch(serverInfoButtonMode) {
		case NONE:
		default:
			serverInfoMode = SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_NONE;
			serverInfoButtonText = null;
			serverInfoURL = null;
			serverInfoHash = null;
			serverInfoEmbedPerms = 0;
			serverInfoEmbedTitle = null;
			serverInfoBlob = null;
			permitChannel = false;
			break;
		case EXTERNAL_URL:
			serverInfoMode = SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_EXTERNAL_URL;
			serverInfoButtonText = this.serverInfoButtonText;
			serverInfoURL = this.serverInfoURL;
			serverInfoHash = null;
			serverInfoEmbedPerms = 0;
			serverInfoEmbedTitle = null;
			serverInfoBlob = null;
			permitChannel = false;
			break;
		case WEBVIEW_URL:
			serverInfoMode = SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP;
			serverInfoButtonText = this.serverInfoButtonText;
			serverInfoURL = this.serverInfoURL;
			serverInfoHash = null;
			serverInfoEmbedPerms = serverInfoPerms != null ? EnumWebViewPerms.toBits(serverInfoPerms) : 0;
			serverInfoEmbedTitle = serverInfoTitle;
			serverInfoBlob = null;
			permitChannel = (serverInfoEmbedPerms & SPacketCustomizePauseMenuV4EAG.SERVER_INFO_EMBED_PERMS_MESSAGE_API) != 0;
			break;
		case WEBVIEW_BLOB:
			serverInfoMode = SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_SHOW_EMBED_OVER_WS;
			serverInfoButtonText = this.serverInfoButtonText;
			serverInfoURL = null;
			serverInfoHash = serverInfoButtonBlobHash.asBytes();
			serverInfoEmbedPerms = serverInfoPerms != null ? EnumWebViewPerms.toBits(serverInfoPerms) : 0;
			serverInfoEmbedTitle = serverInfoTitle;
			serverInfoBlob = serverInfoButtonBlob;
			permitChannel = (serverInfoEmbedPerms & SPacketCustomizePauseMenuV4EAG.SERVER_INFO_EMBED_PERMS_MESSAGE_API) != 0;
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
		Map<String, Integer> imageMappings;
		List<PacketImageData> imageData;
		if(customIcons != null && !customIcons.isEmpty()) {
			imageMappings = new HashMap<>(customIcons.size());
			Map<PacketImageData, Integer> imageDataMap = new HashMap<>(customIcons.size());
			for(Map.Entry<String, PacketImageData> etr : customIcons.entrySet()) {
				int sz = imageDataMap.size();
				Integer i = imageDataMap.putIfAbsent(etr.getValue(), sz);
				if(i != null) {
					imageMappings.put(etr.getKey(), i);
				}else {
					imageMappings.put(etr.getKey(), sz);
				}
			}
			PacketImageData[] imageDataArr = new PacketImageData[imageDataMap.size()];
			for(Map.Entry<PacketImageData, Integer> etr : imageDataMap.entrySet()) {
				imageDataArr[etr.getValue()] = etr.getKey();
			}
			imageData = Arrays.asList(imageDataArr);
		}else {
			imageMappings = null;
			imageData = null;
		}
		return new PauseMenuImplCustom(new SPacketCustomizePauseMenuV4EAG(serverInfoMode, serverInfoButtonText,
				serverInfoURL, serverInfoHash, serverInfoEmbedPerms, serverInfoEmbedTitle, discordButtonMode,
				discordButtonText, discordInviteURL, imageMappings, imageData), serverInfoBlob, permitChannel);
	}

}
