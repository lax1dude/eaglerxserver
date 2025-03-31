package net.lax1dude.eaglercraft.backend.server.base.rpc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.CPacketRPCSetPauseMenuCustom;
import net.lax1dude.eaglercraft.backend.server.api.SHA1Sum;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewProvider;
import net.lax1dude.eaglercraft.backend.server.base.webview.WebViewManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketCustomizePauseMenuV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

class PauseMenuRPCHelper {

	static <PlayerObject> SPacketCustomizePauseMenuV4EAG translateRPCPacket(
			EaglerPlayerRPCManager<PlayerObject> manager, CPacketRPCSetPauseMenuCustom packet) {
		int serverInfoMode;
		String serverInfoButtonText;
		String serverInfoURL;
		byte[] serverInfoHash;
		int serverInfoEmbedPerms;
		String serverInfoEmbedTitle;
		int discordButtonMode;
		String discordButtonText;
		String discordInviteURL;
		Map<String, Integer> imageMappings;
		List<PacketImageData> imageData;
		SPacketCustomizePauseMenuV4EAG parent = null;
		switch(packet.serverInfoMode) {
		case CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_EXTERNAL_URL:
			serverInfoMode = SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_EXTERNAL_URL;
			serverInfoButtonText = packet.serverInfoButtonText;
			serverInfoURL = packet.serverInfoURL;
			serverInfoHash = null;
			serverInfoEmbedTitle = null;
			serverInfoEmbedPerms = 0;
			break;
		case CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP:
			serverInfoMode = SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP;
			serverInfoButtonText = packet.serverInfoButtonText;
			serverInfoURL = packet.serverInfoURL;
			serverInfoHash = null;
			serverInfoEmbedTitle = packet.serverInfoEmbedTitle;
			serverInfoEmbedPerms = packet.serverInfoEmbedPerms;
			break;
		case CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_SHOW_EMBED_OVER_WS:
			serverInfoMode = SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_SHOW_EMBED_OVER_WS;
			serverInfoButtonText = packet.serverInfoButtonText;
			serverInfoURL = null;
			serverInfoHash = packet.serverInfoHash;
			serverInfoEmbedTitle = packet.serverInfoEmbedTitle;
			serverInfoEmbedPerms = packet.serverInfoEmbedPerms;
			break;
		case CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_SHOW_NAMED_EMBED_OVER_WS:
			SHA1Sum mappedHash = handleAlias(manager, packet.serverInfoURL);
			if(mappedHash != null) {
				serverInfoMode = SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_SHOW_EMBED_OVER_WS;
				serverInfoButtonText = packet.serverInfoButtonText;
				serverInfoURL = null;
				serverInfoHash = mappedHash.asBytes();
				serverInfoEmbedTitle = packet.serverInfoEmbedTitle;
				serverInfoEmbedPerms = packet.serverInfoEmbedPerms;
				break;
			}
		case CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_NONE:
		default:
			serverInfoMode = SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_NONE;
			serverInfoButtonText = null;
			serverInfoURL = null;
			serverInfoHash = null;
			serverInfoEmbedTitle = null;
			serverInfoEmbedPerms = 0;
			break;
		case CPacketRPCSetPauseMenuCustom.SERVER_INFO_MODE_INHERIT_DEFAULT:
			if(parent == null) {
				parent = getParent(manager);
			}
			serverInfoMode = parent.serverInfoMode;
			serverInfoButtonText = parent.serverInfoButtonText;
			serverInfoURL = parent.serverInfoURL;
			serverInfoHash = parent.serverInfoHash;
			serverInfoEmbedTitle = parent.serverInfoEmbedTitle;
			serverInfoEmbedPerms = parent.serverInfoEmbedPerms;
			break;
		}
		switch(packet.discordButtonMode) {
		case CPacketRPCSetPauseMenuCustom.DISCORD_MODE_INVITE_URL:
			discordButtonMode = SPacketCustomizePauseMenuV4EAG.DISCORD_MODE_INVITE_URL;
			discordButtonText = packet.discordButtonText;
			discordInviteURL = packet.discordInviteURL;
			break;
		case CPacketRPCSetPauseMenuCustom.DISCORD_MODE_INHERIT_DEFAULT:
			if(parent == null) {
				parent = getParent(manager);
			}
			discordButtonMode = parent.discordButtonMode;
			discordButtonText = parent.discordButtonText;
			discordInviteURL = parent.discordInviteURL;
			break;
		case CPacketRPCSetPauseMenuCustom.DISCORD_MODE_NONE:
		default:
			discordButtonMode = SPacketCustomizePauseMenuV4EAG.DISCORD_MODE_NONE;
			discordButtonText = null;
			discordInviteURL = null;
			break;
		}
		imageMappings = packet.imageMappings;
		if(imageMappings != null && !imageMappings.isEmpty()) {
			if(imageMappings.containsValue(CPacketRPCSetPauseMenuCustom.ICON_ID_INHERIT)) {
				if(parent == null) {
					parent = getParent(manager);
				}
				Map<PacketImageData, Integer> uniqueImages = new HashMap<>();
				Iterator<Entry<String, Integer>> itr = imageMappings.entrySet().iterator();
				while(itr.hasNext()) {
					Entry<String, Integer> etr = itr.next();
					int i = etr.getValue();
					PacketImageData dt;
					if(i == CPacketRPCSetPauseMenuCustom.ICON_ID_INHERIT) {
						eagler: {
							if(parent.imageMappings != null && parent.imageData != null) {
								Integer parentIdx = parent.imageMappings.get(etr.getKey());
								if(parentIdx != null) {
									i = parentIdx;
									if(i >= 0 && i < parent.imageData.size()) {
										dt = parent.imageData.get(i);
										break eagler;
									}
								}
							}
							itr.remove();
							continue;
						}
					}else {
						if(packet.imageData != null && i >= 0 && i < packet.imageData.size()) {
							dt = TextureDataHelper.packetImageDataRPCToCore(packet.imageData.get(i));
						}else {
							itr.remove();
							continue;
						}
					}
					Integer nextIdx = uniqueImages.size();
					Integer ii = uniqueImages.putIfAbsent(dt, nextIdx);
					if(ii == null) {
						etr.setValue(nextIdx);
					}else {
						etr.setValue(ii);
					}
				}
				PacketImageData[] imageDataArr = new PacketImageData[uniqueImages.size()];
				for(Map.Entry<PacketImageData, Integer> etr : uniqueImages.entrySet()) {
					imageDataArr[etr.getValue()] = etr.getKey();
				}
				imageData = Arrays.asList(imageDataArr);
			}else {
				if(packet.imageData != null && !packet.imageData.isEmpty()) {
					imageData = packet.imageData.stream().map(TextureDataHelper::packetImageDataRPCToCore).toList();
				}else {
					// wtf?
					imageData = Collections.emptyList();
				}
			}
		}else {
			imageMappings = null;
			imageData = null;
		}
		return new SPacketCustomizePauseMenuV4EAG(serverInfoMode, serverInfoButtonText, serverInfoURL, serverInfoHash,
				serverInfoEmbedPerms, serverInfoEmbedTitle, discordButtonMode, discordButtonText, discordInviteURL,
				imageMappings, imageData);
	}

	private static <PlayerObject> SHA1Sum handleAlias(EaglerPlayerRPCManager<PlayerObject> manager, String alias) {
		WebViewManager<PlayerObject> mgr = manager.getPlayer().getWebViewManager();
		IWebViewProvider<PlayerObject> provider = mgr.getProvider();
		return provider != null ? provider.handleAlias(mgr, alias) : null;
	}

	private static SPacketCustomizePauseMenuV4EAG getParent(EaglerPlayerRPCManager<?> manager) {
		return manager.getPlayer().getEaglerXServer().getPauseMenuService().getDefaultPauseMenuUnsafe();
	}

}
