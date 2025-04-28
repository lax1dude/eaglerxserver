/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.v1_8.socket.protocol;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.*;

import static net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public enum GamePluginMessageProtocol {
	V3(3,
			define(V3_SKIN_CHANNEL, 0x03, CLIENT_TO_SERVER, CPacketGetOtherSkinEAG.class, CPacketGetOtherSkinEAG::new),
			define(V3_SKIN_CHANNEL, 0x04, SERVER_TO_CLIENT, SPacketOtherSkinPresetEAG.class, SPacketOtherSkinPresetEAG::new),
			define(V3_SKIN_CHANNEL, 0x05, SERVER_TO_CLIENT, SPacketOtherSkinCustomV3EAG.class, SPacketOtherSkinCustomV3EAG::new),
			define(V3_SKIN_CHANNEL, 0x06, CLIENT_TO_SERVER, CPacketGetSkinByURLEAG.class, CPacketGetSkinByURLEAG::new),
			define(V3_SKIN_CHANNEL, 0x07, CLIENT_TO_SERVER, CPacketInstallSkinSPEAG.class, CPacketInstallSkinSPEAG::new),
			define(V3_CAPE_CHANNEL, 0x03, CLIENT_TO_SERVER, CPacketGetOtherCapeEAG.class, CPacketGetOtherCapeEAG::new),
			define(V3_CAPE_CHANNEL, 0x04, SERVER_TO_CLIENT, SPacketOtherCapePresetEAG.class, SPacketOtherCapePresetEAG::new),
			define(V3_CAPE_CHANNEL, 0x05, SERVER_TO_CLIENT, SPacketOtherCapeCustomEAG.class, SPacketOtherCapeCustomEAG::new),
			define(V3_VOICE_CHANNEL, 0x00, SERVER_TO_CLIENT, SPacketVoiceSignalAllowedEAG.class, SPacketVoiceSignalAllowedEAG::new),
			define(V3_VOICE_CHANNEL, 0x00, CLIENT_TO_SERVER, CPacketVoiceSignalRequestEAG.class, CPacketVoiceSignalRequestEAG::new),
			define(V3_VOICE_CHANNEL, 0x01, CLIENT_TO_SERVER, CPacketVoiceSignalConnectEAG.class, CPacketVoiceSignalConnectEAG::new),
			define(V3_VOICE_CHANNEL, 0x01, SERVER_TO_CLIENT, SPacketVoiceSignalConnectV3EAG.class, SPacketVoiceSignalConnectV3EAG::new),
			define(V3_VOICE_CHANNEL, 0x02, CLIENT_TO_SERVER, CPacketVoiceSignalDisconnectV3EAG.class, CPacketVoiceSignalDisconnectV3EAG::new),
			define(V3_VOICE_CHANNEL, 0x02, SERVER_TO_CLIENT, SPacketVoiceSignalDisconnectPeerEAG.class, SPacketVoiceSignalDisconnectPeerEAG::new),
			define(V3_VOICE_CHANNEL, 0x03, CLIENT_TO_SERVER, CPacketVoiceSignalICEEAG.class, CPacketVoiceSignalICEEAG::new),
			define(V3_VOICE_CHANNEL, 0x03, SERVER_TO_CLIENT, SPacketVoiceSignalICEEAG.class, SPacketVoiceSignalICEEAG::new),
			define(V3_VOICE_CHANNEL, 0x04, CLIENT_TO_SERVER, CPacketVoiceSignalDescEAG.class, CPacketVoiceSignalDescEAG::new),
			define(V3_VOICE_CHANNEL, 0x04, SERVER_TO_CLIENT, SPacketVoiceSignalDescEAG.class, SPacketVoiceSignalDescEAG::new),
			define(V3_VOICE_CHANNEL, 0x05, SERVER_TO_CLIENT, SPacketVoiceSignalGlobalEAG.class, SPacketVoiceSignalGlobalEAG::new),
			define(V3_UPDATE_CHANNEL, -1, SERVER_TO_CLIENT, SPacketUpdateCertEAG.class, SPacketUpdateCertEAG::new),
			define(V3_FNAW_EN_CHANNEL, -1, SERVER_TO_CLIENT, SPacketEnableFNAWSkinsEAG.class, SPacketEnableFNAWSkinsEAG::new)
	), V4(4,
			define(V4_CHANNEL, 0x01, CLIENT_TO_SERVER, CPacketGetOtherSkinEAG.class, CPacketGetOtherSkinEAG::new),
			define(V4_CHANNEL, 0x02, SERVER_TO_CLIENT, SPacketOtherSkinPresetEAG.class, SPacketOtherSkinPresetEAG::new),
			define(V4_CHANNEL, 0x03, SERVER_TO_CLIENT, SPacketOtherSkinCustomV4EAG.class, SPacketOtherSkinCustomV4EAG::new),
			define(V4_CHANNEL, 0x04, CLIENT_TO_SERVER, CPacketGetSkinByURLEAG.class, CPacketGetSkinByURLEAG::new),
			define(V4_CHANNEL, 0x05, CLIENT_TO_SERVER, CPacketInstallSkinSPEAG.class, CPacketInstallSkinSPEAG::new),
			define(V4_CHANNEL, 0x06, CLIENT_TO_SERVER, CPacketGetOtherCapeEAG.class, CPacketGetOtherCapeEAG::new),
			define(V4_CHANNEL, 0x07, SERVER_TO_CLIENT, SPacketOtherCapePresetEAG.class, SPacketOtherCapePresetEAG::new),
			define(V4_CHANNEL, 0x08, SERVER_TO_CLIENT, SPacketOtherCapeCustomEAG.class, SPacketOtherCapeCustomEAG::new),
			define(V4_CHANNEL, 0x09, SERVER_TO_CLIENT, SPacketVoiceSignalAllowedEAG.class, SPacketVoiceSignalAllowedEAG::new),
			define(V4_CHANNEL, 0x0A, CLIENT_TO_SERVER, CPacketVoiceSignalRequestEAG.class, CPacketVoiceSignalRequestEAG::new),
			define(V4_CHANNEL, 0x0B, CLIENT_TO_SERVER, CPacketVoiceSignalConnectEAG.class, CPacketVoiceSignalConnectEAG::new),
			define(V4_CHANNEL, 0x0C, SERVER_TO_CLIENT, SPacketVoiceSignalConnectV4EAG.class, SPacketVoiceSignalConnectV4EAG::new),
			define(V4_CHANNEL, 0x0D, SERVER_TO_CLIENT, SPacketVoiceSignalConnectAnnounceV4EAG.class, SPacketVoiceSignalConnectAnnounceV4EAG::new),
			define(V4_CHANNEL, 0x0E, CLIENT_TO_SERVER, CPacketVoiceSignalDisconnectV4EAG.class, CPacketVoiceSignalDisconnectV4EAG::new),
			define(V4_CHANNEL, 0x0F, CLIENT_TO_SERVER, CPacketVoiceSignalDisconnectPeerV4EAG.class, CPacketVoiceSignalDisconnectPeerV4EAG::new),
			define(V4_CHANNEL, 0x10, SERVER_TO_CLIENT, SPacketVoiceSignalDisconnectPeerEAG.class, SPacketVoiceSignalDisconnectPeerEAG::new),
			define(V4_CHANNEL, 0x11, CLIENT_TO_SERVER, CPacketVoiceSignalICEEAG.class, CPacketVoiceSignalICEEAG::new),
			define(V4_CHANNEL, 0x12, SERVER_TO_CLIENT, SPacketVoiceSignalICEEAG.class, SPacketVoiceSignalICEEAG::new),
			define(V4_CHANNEL, 0x13, CLIENT_TO_SERVER, CPacketVoiceSignalDescEAG.class, CPacketVoiceSignalDescEAG::new),
			define(V4_CHANNEL, 0x14, SERVER_TO_CLIENT, SPacketVoiceSignalDescEAG.class, SPacketVoiceSignalDescEAG::new),
			define(V4_CHANNEL, 0x15, SERVER_TO_CLIENT, SPacketVoiceSignalGlobalEAG.class, SPacketVoiceSignalGlobalEAG::new),
			define(V4_CHANNEL, 0x16, SERVER_TO_CLIENT, SPacketUpdateCertEAG.class, SPacketUpdateCertEAG::new),
			define(V4_CHANNEL, 0x17, SERVER_TO_CLIENT, SPacketEnableFNAWSkinsEAG.class, SPacketEnableFNAWSkinsEAG::new),
			define(V4_CHANNEL, 0x18, SERVER_TO_CLIENT, SPacketForceClientSkinPresetV4EAG.class, SPacketForceClientSkinPresetV4EAG::new),
			define(V4_CHANNEL, 0x19, SERVER_TO_CLIENT, SPacketForceClientSkinCustomV4EAG.class, SPacketForceClientSkinCustomV4EAG::new),
			define(V4_CHANNEL, 0x1A, SERVER_TO_CLIENT, SPacketSetServerCookieV4EAG.class, SPacketSetServerCookieV4EAG::new),
			define(V4_CHANNEL, 0x1B, SERVER_TO_CLIENT, SPacketRedirectClientV4EAG.class, SPacketRedirectClientV4EAG::new),
			define(V4_CHANNEL, 0x1C, CLIENT_TO_SERVER, CPacketGetOtherClientUUIDV4EAG.class, CPacketGetOtherClientUUIDV4EAG::new),
			define(V4_CHANNEL, 0x1D, SERVER_TO_CLIENT, SPacketOtherPlayerClientUUIDV4EAG.class, SPacketOtherPlayerClientUUIDV4EAG::new),
			define(V4_CHANNEL, 0x1E, SERVER_TO_CLIENT, SPacketForceClientCapePresetV4EAG.class, SPacketForceClientCapePresetV4EAG::new),
			define(V4_CHANNEL, 0x1F, SERVER_TO_CLIENT, SPacketForceClientCapeCustomV4EAG.class, SPacketForceClientCapeCustomV4EAG::new),
			define(V4_CHANNEL, 0x20, SERVER_TO_CLIENT, SPacketInvalidatePlayerCacheV4EAG.class, SPacketInvalidatePlayerCacheV4EAG::new),
			define(V4_CHANNEL, 0x21, SERVER_TO_CLIENT, SPacketUnforceClientV4EAG.class, SPacketUnforceClientV4EAG::new),
			define(V4_CHANNEL, 0x22, SERVER_TO_CLIENT, SPacketCustomizePauseMenuV4EAG.class, SPacketCustomizePauseMenuV4EAG::new),
			define(V4_CHANNEL, 0x23, CLIENT_TO_SERVER, CPacketRequestServerInfoV4EAG.class, CPacketRequestServerInfoV4EAG::new),
			define(V4_CHANNEL, 0x24, SERVER_TO_CLIENT, SPacketServerInfoDataChunkV4EAG.class, SPacketServerInfoDataChunkV4EAG::new),
			define(V4_CHANNEL, 0x25, CLIENT_TO_SERVER, CPacketWebViewMessageEnV4EAG.class, CPacketWebViewMessageEnV4EAG::new),
			define(V4_CHANNEL, 0x26, CLIENT_TO_SERVER, CPacketWebViewMessageV4EAG.class, CPacketWebViewMessageV4EAG::new),
			define(V4_CHANNEL, 0x27, SERVER_TO_CLIENT, SPacketWebViewMessageV4EAG.class, SPacketWebViewMessageV4EAG::new),
			define(V4_CHANNEL, 0x28, SERVER_TO_CLIENT, SPacketNotifIconsRegisterV4EAG.class, SPacketNotifIconsRegisterV4EAG::new),
			define(V4_CHANNEL, 0x29, SERVER_TO_CLIENT, SPacketNotifIconsReleaseV4EAG.class, SPacketNotifIconsReleaseV4EAG::new),
			define(V4_CHANNEL, 0x2A, SERVER_TO_CLIENT, SPacketNotifBadgeShowV4EAG.class, SPacketNotifBadgeShowV4EAG::new),
			define(V4_CHANNEL, 0x2B, SERVER_TO_CLIENT, SPacketNotifBadgeHideV4EAG.class, SPacketNotifBadgeHideV4EAG::new)
	), V5(5,

			// Client to server
			define(0x01, CLIENT_TO_SERVER, CPacketGetOtherSkinV5EAG.class, CPacketGetOtherSkinV5EAG::new),
			define(0x02, CLIENT_TO_SERVER, CPacketGetOtherCapeV5EAG.class, CPacketGetOtherCapeV5EAG::new),
			define(0x03, CLIENT_TO_SERVER, CPacketGetOtherTexturesV5EAG.class, CPacketGetOtherTexturesV5EAG::new),
			define(0x04, CLIENT_TO_SERVER, CPacketGetSkinByURLV5EAG.class, CPacketGetSkinByURLV5EAG::new),
			define(0x05, CLIENT_TO_SERVER, CPacketInstallSkinSPEAG.class, CPacketInstallSkinSPEAG::new),
			define(0x06, CLIENT_TO_SERVER, CPacketVoiceSignalRequestEAG.class, CPacketVoiceSignalRequestEAG::new),
			define(0x07, CLIENT_TO_SERVER, CPacketVoiceSignalConnectEAG.class, CPacketVoiceSignalConnectEAG::new),
			define(0x08, CLIENT_TO_SERVER, CPacketVoiceSignalDisconnectV4EAG.class, CPacketVoiceSignalDisconnectV4EAG::new),
			define(0x09, CLIENT_TO_SERVER, CPacketVoiceSignalDisconnectPeerV4EAG.class, CPacketVoiceSignalDisconnectPeerV4EAG::new),
			define(0x0A, CLIENT_TO_SERVER, CPacketVoiceSignalICEEAG.class, CPacketVoiceSignalICEEAG::new),
			define(0x0B, CLIENT_TO_SERVER, CPacketVoiceSignalDescEAG.class, CPacketVoiceSignalDescEAG::new),
			define(0x0C, CLIENT_TO_SERVER, CPacketGetOtherClientUUIDV4EAG.class, CPacketGetOtherClientUUIDV4EAG::new),
			define(0x0D, CLIENT_TO_SERVER, CPacketRequestServerInfoV4EAG.class, CPacketRequestServerInfoV4EAG::new),
			define(0x0E, CLIENT_TO_SERVER, CPacketWebViewMessageEnV4EAG.class, CPacketWebViewMessageEnV4EAG::new),
			define(0x0F, CLIENT_TO_SERVER, CPacketWebViewMessageV4EAG.class, CPacketWebViewMessageV4EAG::new),

			// Server to client
			define(0x01, SERVER_TO_CLIENT, SPacketOtherSkinPresetV5EAG.class, SPacketOtherSkinPresetV5EAG::new),
			define(0x02, SERVER_TO_CLIENT, SPacketOtherSkinCustomV5EAG.class, SPacketOtherSkinCustomV5EAG::new),
			define(0x03, SERVER_TO_CLIENT, SPacketOtherCapePresetV5EAG.class, SPacketOtherCapePresetV5EAG::new),
			define(0x04, SERVER_TO_CLIENT, SPacketOtherCapeCustomV5EAG.class, SPacketOtherCapeCustomV5EAG::new),
			define(0x05, SERVER_TO_CLIENT, SPacketOtherTexturesV5EAG.class, SPacketOtherTexturesV5EAG::new),
			define(0x06, SERVER_TO_CLIENT, SPacketVoiceSignalAllowedEAG.class, SPacketVoiceSignalAllowedEAG::new),
			define(0x07, SERVER_TO_CLIENT, SPacketVoiceSignalConnectV4EAG.class, SPacketVoiceSignalConnectV4EAG::new),
			define(0x08, SERVER_TO_CLIENT, SPacketVoiceSignalConnectAnnounceV4EAG.class, SPacketVoiceSignalConnectAnnounceV4EAG::new),
			define(0x09, SERVER_TO_CLIENT, SPacketVoiceSignalDisconnectPeerEAG.class, SPacketVoiceSignalDisconnectPeerEAG::new),
			define(0x0A, SERVER_TO_CLIENT, SPacketVoiceSignalICEEAG.class, SPacketVoiceSignalICEEAG::new),
			define(0x0B, SERVER_TO_CLIENT, SPacketVoiceSignalDescEAG.class, SPacketVoiceSignalDescEAG::new),
			define(0x0C, SERVER_TO_CLIENT, SPacketVoiceSignalGlobalEAG.class, SPacketVoiceSignalGlobalEAG::new),
			define(0x0D, SERVER_TO_CLIENT, SPacketUpdateCertEAG.class, SPacketUpdateCertEAG::new),
			define(0x0E, SERVER_TO_CLIENT, SPacketEnableFNAWSkinsEAG.class, SPacketEnableFNAWSkinsEAG::new),
			define(0x0F, SERVER_TO_CLIENT, SPacketForceClientSkinPresetV4EAG.class, SPacketForceClientSkinPresetV4EAG::new),
			define(0x10, SERVER_TO_CLIENT, SPacketForceClientSkinCustomV4EAG.class, SPacketForceClientSkinCustomV4EAG::new),
			define(0x11, SERVER_TO_CLIENT, SPacketSetServerCookieV4EAG.class, SPacketSetServerCookieV4EAG::new),
			define(0x12, SERVER_TO_CLIENT, SPacketRedirectClientV4EAG.class, SPacketRedirectClientV4EAG::new),
			define(0x13, SERVER_TO_CLIENT, SPacketOtherPlayerClientUUIDV4EAG.class, SPacketOtherPlayerClientUUIDV4EAG::new),
			define(0x14, SERVER_TO_CLIENT, SPacketForceClientCapePresetV4EAG.class, SPacketForceClientCapePresetV4EAG::new),
			define(0x15, SERVER_TO_CLIENT, SPacketForceClientCapeCustomV4EAG.class, SPacketForceClientCapeCustomV4EAG::new),
			define(0x16, SERVER_TO_CLIENT, SPacketInvalidatePlayerCacheV4EAG.class, SPacketInvalidatePlayerCacheV4EAG::new),
			define(0x17, SERVER_TO_CLIENT, SPacketUnforceClientV4EAG.class, SPacketUnforceClientV4EAG::new),
			define(0x18, SERVER_TO_CLIENT, SPacketCustomizePauseMenuV4EAG.class, SPacketCustomizePauseMenuV4EAG::new),
			define(0x19, SERVER_TO_CLIENT, SPacketServerInfoDataChunkV4EAG.class, SPacketServerInfoDataChunkV4EAG::new),
			define(0x1A, SERVER_TO_CLIENT, SPacketDisplayWebViewURLV5EAG.class, SPacketDisplayWebViewURLV5EAG::new),
			define(0x1B, SERVER_TO_CLIENT, SPacketDisplayWebViewBlobV5EAG.class, SPacketDisplayWebViewBlobV5EAG::new),
			define(0x1C, SERVER_TO_CLIENT, SPacketWebViewMessageV4EAG.class, SPacketWebViewMessageV4EAG::new),
			define(0x1D, SERVER_TO_CLIENT, SPacketNotifIconsRegisterV4EAG.class, SPacketNotifIconsRegisterV4EAG::new),
			define(0x1E, SERVER_TO_CLIENT, SPacketNotifIconsReleaseV4EAG.class, SPacketNotifIconsReleaseV4EAG::new),
			define(0x1F, SERVER_TO_CLIENT, SPacketNotifBadgeShowV4EAG.class, SPacketNotifBadgeShowV4EAG::new),
			define(0x20, SERVER_TO_CLIENT, SPacketNotifBadgeHideV4EAG.class, SPacketNotifBadgeHideV4EAG::new),
			define(0x21, SERVER_TO_CLIENT, SPacketClientStateFlagV5EAG.class, SPacketClientStateFlagV5EAG::new)
	);

	public final int ver;

	private final Map<String, Object>[] channelMap;
	private final PacketDef[][] channelMapV5;
	private final Map<Class<? extends GameMessagePacket>, PacketDef>[] classMap;
	private final Set<String> notChannelMap = new HashSet<>(); // populated in clinit

	private static final GamePluginMessageProtocol[] PROTOCOLS_MAP = new GamePluginMessageProtocol[6];
	private static final Set<String> allChannels = new HashSet<>();

	private GamePluginMessageProtocol(int versionInt, PacketDef... packets) {
		ver = versionInt;
		if (versionInt >= 5) {
			channelMap = null;
			classMap = new Map[] { new HashMap<>(), new HashMap<>() };
			this.channelMapV5 = new PacketDef[2][48];
			for (int i = 0; i < packets.length; ++i) {
				PacketDef pkt = packets[i];
				classMap[pkt.dir].put(pkt.clazz, pkt);
				channelMapV5[pkt.dir][pkt.id] = pkt;
			}
		} else {
			channelMap = new Map[] { new HashMap<>(), new HashMap<>() };
			classMap = new Map[] { new HashMap<>(), new HashMap<>() };
			channelMapV5 = null;
			for (int i = 0; i < packets.length; ++i) {
				PacketDef pkt = packets[i];
				classMap[pkt.dir].put(pkt.clazz, pkt);
				if (pkt.id == -1) {
					channelMap[pkt.dir].put(pkt.channel, pkt);
				} else {
					PacketDef[] map = (PacketDef[]) channelMap[pkt.dir].get(pkt.channel);
					if (map == null || map.length <= pkt.id) {
						PacketDef[] newMap = new PacketDef[((pkt.id + 1) & 0xF0) + 0x0F];
						if (map != null) {
							System.arraycopy(map, 0, newMap, 0, map.length);
						}
						map = newMap;
						channelMap[pkt.dir].put(pkt.channel, map);
					}
					map[pkt.id] = pkt;
				}
			}
		}
	}

	private static PacketDef define(String channel, int id, int dir, Class<? extends GameMessagePacket> clazz,
			Supplier<? extends GameMessagePacket> ctor) {
		return new PacketDef(channel, id, dir, clazz, ctor);
	}

	private static PacketDef define(int id, int dir, Class<? extends GameMessagePacket> clazz,
			Supplier<? extends GameMessagePacket> ctor) {
		return new PacketDef(null, id, dir, clazz, ctor);
	}

	private static class PacketDef {

		private final String channel;
		private final int id;
		private final int dir;
		private final Class<? extends GameMessagePacket> clazz;
		private final Supplier<? extends GameMessagePacket> ctor;

		private PacketDef(String channel, int id, int dir, Class<? extends GameMessagePacket> clazz,
				Supplier<? extends GameMessagePacket> ctor) {
			this.channel = channel;
			this.id = id;
			this.dir = dir;
			this.clazz = clazz;
			this.ctor = ctor;
		}

	}

	public GameMessagePacket readPacket(String channel, int direction, GamePacketInputBuffer buffer)
			throws IOException {
		Object obj = channelMap[direction].get(channel);
		if (obj == null) {
			return null;
		}
		PacketDef toRead;
		if (obj instanceof PacketDef) {
			toRead = (PacketDef) obj;
		} else {
			int pktId = buffer.readUnsignedByte();
			PacketDef[] pkts = (PacketDef[]) obj;
			if (pktId < 0 || pktId >= pkts.length || (toRead = pkts[pktId]) == null) {
				throw new IOException("[" + channel + "] Unknown packet ID: " + pktId);
			}
		}
		GameMessagePacket ret = toRead.ctor.get();
		ret.readPacket(buffer);
		return ret;
	}

	public GameMessagePacket readPacketV5(int direction, GamePacketInputBuffer buffer) throws IOException {
		PacketDef[] lst = channelMapV5[direction];
		int pktId = buffer.readUnsignedByte();
		PacketDef def;
		if (pktId >= lst.length || (def = lst[pktId]) == null) {
			throw new IOException("Unknown packet ID: " + pktId);
		}
		GameMessagePacket ret = def.ctor.get();
		ret.readPacket(buffer);
		return ret;
	}

	public String writePacket(int direction, GamePacketOutputBuffer stream, GameMessagePacket packet)
			throws IOException {
		Class<? extends GameMessagePacket> clazz = packet.getClass();
		PacketDef def = classMap[direction].get(clazz);
		if (def == null) {
			throw new IOException("Unknown packet type or wrong direction: " + clazz);
		}
		if (def.id != -1) {
			stream.writeByte(def.id);
		}
		packet.writePacket(stream);
		return def.channel;
	}

	public void writePacketV5(int direction, GamePacketOutputBuffer stream, GameMessagePacket packet)
			throws IOException {
		Class<? extends GameMessagePacket> clazz = packet.getClass();
		PacketDef def = classMap[direction].get(clazz);
		if (def == null) {
			throw new IOException("Unknown packet type or wrong direction: " + clazz);
		}
		if (def.id != -1) {
			stream.writeByte(def.id);
		}
		packet.writePacket(stream);
	}

	public List<String> filterProtocols(Collection<String> data) {
		List<String> ret = new ArrayList<>(data.size());
		for (String str : data) {
			if (!notChannelMap.contains(str)) {
				ret.add(str);
			}
		}
		return ret;
	}

	public static GamePluginMessageProtocol getByVersion(int ver) {
		if (ver < 0 || ver >= PROTOCOLS_MAP.length) {
			return null;
		}
		return PROTOCOLS_MAP[ver];
	}

	public static Set<String> getAllChannels() {
		return allChannels;
	}

	static {
		GamePluginMessageProtocol[] _values = values();
		PROTOCOLS_MAP[2] = V3;
		for (int i = 0; i < _values.length; ++i) {
			GamePluginMessageProtocol protocol = _values[i];
			PROTOCOLS_MAP[protocol.ver] = protocol;
			if (protocol.ver < 5) {
				allChannels.addAll(protocol.channelMap[CLIENT_TO_SERVER].keySet());
				allChannels.addAll(protocol.channelMap[SERVER_TO_CLIENT].keySet());
			}
		}
		for (int i = 0; i < _values.length; ++i) {
			GamePluginMessageProtocol protocol = _values[i];
			if (protocol.ver < 5) {
				protocol.notChannelMap.addAll(allChannels);
				protocol.notChannelMap.removeAll(protocol.channelMap[CLIENT_TO_SERVER].keySet());
				protocol.notChannelMap.removeAll(protocol.channelMap[SERVER_TO_CLIENT].keySet());
			}
		}
	}
}
