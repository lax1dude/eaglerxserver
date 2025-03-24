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

package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import static net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket.readString;
import static net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket.writeString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.rpc.protocol.util.PacketImageData;

public class CPacketRPCSetPauseMenuCustom implements EaglerBackendRPCPacket {

	public static final int SERVER_INFO_MODE_NONE = 0;
	public static final int SERVER_INFO_MODE_EXTERNAL_URL = 1;
	public static final int SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP = 2;
	public static final int SERVER_INFO_MODE_SHOW_EMBED_OVER_WS = 3;
	public static final int SERVER_INFO_MODE_INHERIT_DEFAULT = 4;

	public static final int SERVER_INFO_EMBED_PERMS_JAVASCRIPT = 1;
	public static final int SERVER_INFO_EMBED_PERMS_MESSAGE_API = 2;
	public static final int SERVER_INFO_EMBED_PERMS_STRICT_CSP = 4;

	public static final int DISCORD_MODE_NONE = 0;
	public static final int DISCORD_MODE_INVITE_URL = 1;
	public static final int DISCORD_MODE_INHERIT_DEFAULT = 2;

	public int serverInfoMode;
	public String serverInfoButtonText;
	public String serverInfoURL;
	public byte[] serverInfoHash;
	public int serverInfoEmbedPerms;
	public String serverInfoEmbedTitle;
	public int discordButtonMode;
	public String discordButtonText;
	public String discordInviteURL;

	public Map<String,Integer> imageMappings;
	public List<PacketImageData> imageData;

	public CPacketRPCSetPauseMenuCustom() {
	}

	public CPacketRPCSetPauseMenuCustom(int serverInfoMode, String serverInfoButtonText, String serverInfoURL,
			byte[] serverInfoHash, int serverInfoEmbedPerms, String serverInfoEmbedTitle, int discordButtonMode,
			String discordButtonText, String discordInviteURL, Map<String, Integer> imageMappings,
			List<PacketImageData> imageData) {
		this.serverInfoMode = serverInfoMode;
		this.serverInfoButtonText = serverInfoButtonText;
		this.serverInfoURL = serverInfoURL;
		this.serverInfoHash = serverInfoHash;
		this.serverInfoEmbedPerms = serverInfoEmbedPerms;
		this.serverInfoEmbedTitle = serverInfoEmbedTitle;
		this.discordButtonMode = discordButtonMode;
		this.discordButtonText = discordButtonText;
		this.discordInviteURL = discordInviteURL;
		this.imageMappings = imageMappings;
		this.imageData = imageData;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		imageMappings = null;
		imageData = null;
		int flags = buffer.readUnsignedByte();
		serverInfoMode = (flags & 15);
		discordButtonMode = ((flags >> 4) & 15);
		switch(serverInfoMode) {
		case SERVER_INFO_MODE_EXTERNAL_URL:
			serverInfoButtonText = readString(buffer, 127, false, StandardCharsets.UTF_8);
			serverInfoURL = readString(buffer, 65535, true, StandardCharsets.US_ASCII);
			serverInfoEmbedPerms = 0;
			serverInfoHash = null;
			break;
		case SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP:
			serverInfoButtonText = readString(buffer, 127, false, StandardCharsets.UTF_8);
			serverInfoURL = readString(buffer, 65535, true, StandardCharsets.US_ASCII);
			serverInfoEmbedPerms = buffer.readUnsignedByte();
			serverInfoHash = null;
			serverInfoEmbedTitle = readString(buffer, 127, false, StandardCharsets.UTF_8);
			break;
		case SERVER_INFO_MODE_SHOW_EMBED_OVER_WS:
			serverInfoButtonText = readString(buffer, 127, false, StandardCharsets.UTF_8);
			serverInfoURL = null;
			serverInfoEmbedPerms = buffer.readUnsignedByte();
			serverInfoHash = new byte[20];
			serverInfoEmbedTitle = readString(buffer, 127, false, StandardCharsets.UTF_8);
			buffer.readFully(serverInfoHash);
			break;
		case SERVER_INFO_MODE_INHERIT_DEFAULT:
		default:
			serverInfoButtonText = null;
			serverInfoURL = null;
			serverInfoEmbedPerms = 0;
			serverInfoHash = null;
			break;
		}
		if(discordButtonMode == DISCORD_MODE_INVITE_URL) {
			discordButtonText = readString(buffer, 127, false, StandardCharsets.UTF_8);
			discordInviteURL = readString(buffer, 65535, true, StandardCharsets.US_ASCII);
		}else {
			discordButtonText = null;
			discordInviteURL = null;
		}
		int mappingsCount = buffer.readUnsignedShort();
		if(mappingsCount > 0) {
			imageMappings = new HashMap<>();
			imageData = new ArrayList<>();
			for(int i = 0; i < mappingsCount; ++i) {
				imageMappings.put(readString(buffer, 255, false, StandardCharsets.US_ASCII), buffer.readUnsignedShort());
			}
			int imageDataCount = buffer.readUnsignedShort();
			for(int i = 0; i < imageDataCount; ++i) {
				int w = buffer.readUnsignedByte();
				int h = buffer.readUnsignedByte();
				int pixelCount = w * h;
				int[] pixels = new int[pixelCount];
				for(int j = 0, p, pR, pG, pB; j < pixelCount; ++j) {
					p = buffer.readUnsignedShort();
					pR = (p >>> 11) & 0x1F;
					pG = (p >>> 5) & 0x3F;
					pB = p & 0x1F;
					if(pR + pG + pB > 0) {
						pB = (pB - 1) * 31 / 30;
						pixels[j] = 0xFF000000 | (pR << 19) | (pG << 10) | (pB << 3);
					}else {
						pixels[j] = 0;
					}
				}
				imageData.add(new PacketImageData(w, h, pixels));
			}
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeByte(serverInfoMode | (discordButtonMode << 4));
		switch(serverInfoMode) {
		case SERVER_INFO_MODE_EXTERNAL_URL:
			writeString(buffer, serverInfoButtonText, false, StandardCharsets.UTF_8);
			writeString(buffer, serverInfoURL, true, StandardCharsets.US_ASCII);
			break;
		case SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP:
			writeString(buffer, serverInfoButtonText, false, StandardCharsets.UTF_8);
			writeString(buffer, serverInfoURL, true, StandardCharsets.US_ASCII);
			buffer.writeByte(serverInfoEmbedPerms);
			writeString(buffer, serverInfoEmbedTitle, false, StandardCharsets.UTF_8);
			break;
		case SERVER_INFO_MODE_SHOW_EMBED_OVER_WS:
			writeString(buffer, serverInfoButtonText, false, StandardCharsets.UTF_8);
			buffer.writeByte(serverInfoEmbedPerms);
			writeString(buffer, serverInfoEmbedTitle, false, StandardCharsets.UTF_8);
			if(serverInfoHash.length != 20) {
				throw new IOException("Hash must be 20 bytes! (" + serverInfoHash.length + " given)");
			}
			buffer.write(serverInfoHash);
			break;
		case SERVER_INFO_MODE_INHERIT_DEFAULT:
		default:
			break;
		}
		if(discordButtonMode == DISCORD_MODE_INVITE_URL) {
			writeString(buffer, discordButtonText, false, StandardCharsets.UTF_8);
			writeString(buffer, discordInviteURL, true, StandardCharsets.US_ASCII);
		}
		if(imageMappings != null && !imageMappings.isEmpty()) {
			buffer.writeShort(imageMappings.size());
			for(Entry<String,Integer> etr : imageMappings.entrySet()) {
				writeString(buffer, etr.getKey(), false, StandardCharsets.US_ASCII);
				buffer.writeShort(etr.getValue().intValue());
			}
			buffer.writeShort(imageData.size());
			for(PacketImageData etr : imageData) {
				if(etr.width < 1 || etr.width > 64 || etr.height < 1 || etr.height > 64) {
					throw new IOException("Invalid image dimensions in packet, must be between 1x1 and 64x64, got " + etr.width + "x" + etr.height);
				}
				PacketImageData.writeRGB16(buffer, etr);
			}
		}else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return -1;
	}

}