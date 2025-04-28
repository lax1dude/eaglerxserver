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

package net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public class SPacketCustomizePauseMenuV4EAG implements GameMessagePacket {

	public static final int SERVER_INFO_MODE_NONE = 0;
	public static final int SERVER_INFO_MODE_EXTERNAL_URL = 1;
	public static final int SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP = 2;
	public static final int SERVER_INFO_MODE_SHOW_EMBED_OVER_WS = 3;

	public static final int SERVER_INFO_EMBED_PERMS_JAVASCRIPT = 1;
	public static final int SERVER_INFO_EMBED_PERMS_MESSAGE_API = 2;
	public static final int SERVER_INFO_EMBED_PERMS_STRICT_CSP = 4;

	public static final int DISCORD_MODE_NONE = 0;
	public static final int DISCORD_MODE_INVITE_URL = 1;

	public int serverInfoMode;
	public String serverInfoButtonText;
	public String serverInfoURL;
	public byte[] serverInfoHash;
	public int serverInfoEmbedPerms;
	public String serverInfoEmbedTitle;
	public int discordButtonMode;
	public String discordButtonText;
	public String discordInviteURL;

	public Map<String, Integer> imageMappings;
	public List<PacketImageData> imageData;

	public SPacketCustomizePauseMenuV4EAG() {
	}

	public SPacketCustomizePauseMenuV4EAG(int serverInfoMode, String serverInfoButtonText, String serverInfoURL,
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
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		imageMappings = null;
		imageData = null;
		int flags = buffer.readUnsignedByte();
		serverInfoMode = (flags & 15);
		discordButtonMode = ((flags >> 4) & 15);
		switch (serverInfoMode) {
		case SERVER_INFO_MODE_EXTERNAL_URL:
			serverInfoButtonText = buffer.readStringMC(127);
			serverInfoURL = buffer.readStringEaglerASCII16();
			serverInfoEmbedPerms = 0;
			serverInfoHash = null;
			break;
		case SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP:
			serverInfoButtonText = buffer.readStringMC(127);
			serverInfoURL = buffer.readStringEaglerASCII16();
			serverInfoEmbedPerms = buffer.readUnsignedByte();
			serverInfoHash = null;
			serverInfoEmbedTitle = buffer.readStringMC(127);
			break;
		case SERVER_INFO_MODE_SHOW_EMBED_OVER_WS:
			serverInfoButtonText = buffer.readStringMC(127);
			serverInfoURL = null;
			serverInfoEmbedPerms = buffer.readUnsignedByte();
			serverInfoHash = new byte[20];
			serverInfoEmbedTitle = buffer.readStringMC(127);
			buffer.readFully(serverInfoHash);
			break;
		default:
			serverInfoButtonText = null;
			serverInfoURL = null;
			serverInfoEmbedPerms = 0;
			serverInfoHash = null;
			break;
		}
		if (discordButtonMode == DISCORD_MODE_INVITE_URL) {
			discordButtonText = buffer.readStringMC(127);
			discordInviteURL = buffer.readStringEaglerASCII16();
		} else {
			discordButtonText = null;
			discordInviteURL = null;
		}
		int mappingsCount = buffer.readVarInt();
		if (mappingsCount > 0) {
			imageMappings = new HashMap<>();
			imageData = new ArrayList<>();
			for (int i = 0; i < mappingsCount; ++i) {
				imageMappings.put(buffer.readStringEaglerASCII8(), buffer.readVarInt());
			}
			int imageDataCount = buffer.readVarInt();
			for (int i = 0; i < imageDataCount; ++i) {
				imageData.add(PacketImageData.readRGB16(buffer));
			}
		}
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		buffer.writeByte(serverInfoMode | (discordButtonMode << 4));
		switch (serverInfoMode) {
		case SERVER_INFO_MODE_EXTERNAL_URL:
			buffer.writeStringMC(serverInfoButtonText);
			buffer.writeStringEaglerASCII16(serverInfoURL);
			break;
		case SERVER_INFO_MODE_SHOW_EMBED_OVER_HTTP:
			buffer.writeStringMC(serverInfoButtonText);
			buffer.writeStringEaglerASCII16(serverInfoURL);
			buffer.writeByte(serverInfoEmbedPerms);
			buffer.writeStringMC(serverInfoEmbedTitle);
			break;
		case SERVER_INFO_MODE_SHOW_EMBED_OVER_WS:
			buffer.writeStringMC(serverInfoButtonText);
			buffer.writeByte(serverInfoEmbedPerms);
			buffer.writeStringMC(serverInfoEmbedTitle);
			if (serverInfoHash.length != 20) {
				throw new IOException("Hash must be 20 bytes! (" + serverInfoHash.length + " given)");
			}
			buffer.write(serverInfoHash);
			break;
		default:
			break;
		}
		if (discordButtonMode == DISCORD_MODE_INVITE_URL) {
			buffer.writeStringMC(discordButtonText);
			buffer.writeStringEaglerASCII16(discordInviteURL);
		}
		if (imageMappings != null && !imageMappings.isEmpty()) {
			buffer.writeVarInt(imageMappings.size());
			for (Entry<String, Integer> etr : imageMappings.entrySet()) {
				buffer.writeStringEaglerASCII8(etr.getKey());
				buffer.writeVarInt(etr.getValue().intValue());
			}
			buffer.writeVarInt(imageData.size());
			for (PacketImageData etr : imageData) {
				if (etr.width < 1 || etr.width > 64 || etr.height < 1 || etr.height > 64) {
					throw new IOException("Invalid image dimensions in packet, must be between 1x1 and 64x64, got "
							+ etr.width + "x" + etr.height);
				}
				PacketImageData.writeRGB16(buffer, etr);
			}
		} else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return -1;
	}

}
