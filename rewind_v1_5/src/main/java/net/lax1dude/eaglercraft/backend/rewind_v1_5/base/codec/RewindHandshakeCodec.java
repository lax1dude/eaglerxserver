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

package net.lax1dude.eaglercraft.backend.rewind_v1_5.base.codec;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.base.RewindProtocol;
import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IPacket2ClientProtocol;

public class RewindHandshakeCodec<PlayerObject> extends RewindChannelHandler.Codec<PlayerObject> {

	private static final int CAPABILITIES_MASK = EnumCapabilityType.VOICE.getBit() | EnumCapabilityType.REDIRECT.getBit();
	private static final int[] CAPABILITIES_VER = new int[] { 1, 1 };

	private static final byte[] REWIND_STR = "rewind".getBytes(StandardCharsets.US_ASCII);
	private static final byte[] SKIN_V1_STR = "skin_v1".getBytes(StandardCharsets.US_ASCII);
	private static final byte[] CAPE_V1_STR = "cape_v1".getBytes(StandardCharsets.US_ASCII);
	private static final byte[] BRAND_UUID_V1_STR = "brand_uuid_v1".getBytes(StandardCharsets.US_ASCII);
	private static final byte[] ERR = new byte[0];

	protected static final int STATE_STALLING = 0;
	protected static final int STATE_SENT_HANDSHAKE = 1;
	protected static final int STATE_SENT_REQUESTED_LOGIN = 2;
	protected static final int STATE_SENT_RECEIVED_ALLOW_LOGIN = 3;
	protected static final int STATE_SENT_FINISH_LOGIN = 4;
	protected static final int STATE_COMPLETED = 5;

	protected int state = STATE_SENT_HANDSHAKE;
	protected String username;
	protected byte[] skinData;
	protected byte[] capeData;

	public RewindHandshakeCodec(IPacket2ClientProtocol firstPacket) {
		this.username = firstPacket.getUsername();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		if(state != STATE_COMPLETED) {
			try {
				int type = buf.readUnsignedByte();
				switch(type) {
				case 0xCD: // Packet205ClientCommand
					handleClientClientCommand(ctx, buf, out);
					break;
				case 0xFA: // Packet250CustomPayload
					handleClientCustomPayload(ctx, buf);
					break;
				default:
					handleUnexpectedClientPacket(ctx, type);
					break;
				}
			}catch(IndexOutOfBoundsException ex) {
				state = STATE_COMPLETED;
				ctx.close();
			}
		}
	}

	private void handleClientClientCommand(ChannelHandlerContext ctx, ByteBuf buf, List<Object> output) {
		if(buf.readUnsignedByte() != 0) {
			handleUnexpectedClientPacket(ctx, 0xCD);
		}
		if(buf.isReadable()) {
			throw new IndexOutOfBoundsException();
		}
		if(state == STATE_SENT_RECEIVED_ALLOW_LOGIN) {
			state = STATE_STALLING;
			boolean skin = false, cape = false;
			int total = 1;
			if(skinData != null && skinData != ERR) {
				skin = true;
				++total;
			}
			if(capeData != null && capeData != ERR) {
				cape = true;
				++total;
			}
			ByteBuf packet = ctx.alloc().buffer();
			try {
				// PROTOCOL_CLIENT_PROFILE_DATA
				packet.writeByte(0x07);
				packet.writeByte(total);
				if(skin) {
					packet.writeByte(SKIN_V1_STR.length);
					packet.writeBytes(SKIN_V1_STR);
					packet.writeShort(skinData.length);
					packet.writeBytes(skinData);
				}
				if(cape) {
					packet.writeByte(CAPE_V1_STR.length);
					packet.writeBytes(CAPE_V1_STR);
					packet.writeShort(capeData.length);
					packet.writeBytes(capeData);
				}
				packet.writeByte(BRAND_UUID_V1_STR.length);
				packet.writeBytes(BRAND_UUID_V1_STR);
				packet.writeShort(16);
				UUID uuid = RewindProtocol.BRAND_EAGLERXREWIND_1_5_2;
				packet.writeLong(uuid.getMostSignificantBits());
				packet.writeLong(uuid.getLeastSignificantBits());
				output.add(packet.retain());
			}finally {
				packet.release();
				skinData = null;
				capeData = null;
			}
			packet = ctx.alloc().buffer();
			try {
				// PROTOCOL_CLIENT_FINISH_LOGIN
				packet.writeByte(0x08);
				state = STATE_SENT_FINISH_LOGIN;
				output.add(packet.retain());
			}finally {
				packet.release();
			}
		}else {
			handleUnexpectedClientPacket(ctx, 0xCD);
		}
	}

	private void handleClientCustomPayload(ChannelHandlerContext ctx, ByteBuf buf) {
		if(state == STATE_SENT_HANDSHAKE || state == STATE_SENT_REQUESTED_LOGIN || state == STATE_SENT_RECEIVED_ALLOW_LOGIN) {
			String channelName = BufferUtils.readLegacyMCString(buf, 20);
			if("EAG|MySkin".equals(channelName)) {
				int len = buf.readShort();
				if(len > 0 && len < 32767) {
					ByteBuf buf2 = buf.readSlice(len);
					if(buf.isReadable()) {
						throw new IndexOutOfBoundsException();
					}
					handleEagMySkin(ctx, buf2);
				}
			}else if("EAG|MyCape".equals(channelName)) {
				int len = buf.readShort();
				if(len > 0 && len < 32767) {
					ByteBuf buf2 = buf.readSlice(len);
					if(buf.isReadable()) {
						throw new IndexOutOfBoundsException();
					}
					handleEagMyCape(ctx, buf2);
				}
			}else {
				handleUnexpectedClientPacket(ctx, 0xFA);
			}
		}else {
			handleUnexpectedClientPacket(ctx, 0xFA);
		}
	}

	private void handleEagMySkin(ChannelHandlerContext ctx, ByteBuf data) {
		if(skinData != null) {
			handleUnexpectedClientPacket(ctx, 0xFA);
			return;
		}
		skinData = SkinPacketUtils.rewriteLegacyHandshakeSkinToV1(data);
		if(skinData == null) {
			skinData = ERR;
		}
	}

	private void handleEagMyCape(ChannelHandlerContext ctx, ByteBuf data) {
		if(capeData != null) {
			handleUnexpectedClientPacket(ctx, 0xFA);
			return;
		}
		capeData = SkinPacketUtils.rewriteLegacyHandshakeCapeToV1(data);
		if(capeData == null) {
			capeData = ERR;
		}
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		if(state != STATE_COMPLETED && buf.readableBytes() >= 1) {
			try {
				int type = buf.readUnsignedByte();
				switch(type) {
				case 0x02: // PROTOCOL_SERVER_VERISON
					handleServerVersion(ctx, buf);
					break;
				case 0x03: // PROTOCOL_VERISON_MISMATCH
					handleServerVersionMismatch(ctx, buf);
					break;
				case 0x05: // PROTOCOL_SERVER_ALLOW_LOGIN
					handleServerAllowLogin(ctx, buf, out);
					break;
				case 0x06: // PROTOCOL_SERVER_DENY_LOGIN
					handleServerDenyLogin(ctx, buf);
					break;
				case 0x09: // PROTOCOL_SERVER_FINISH_LOGIN
					handleServerFinishLogin(ctx, buf);
					break;
				case 0x0A: // PROTOCOL_SERVER_REDIRECT_TO
					handleServerRedirectTo(ctx, buf);
					break;
				case 0xFF: // PROTOCOL_SERVER_ERROR
					handleServerError(ctx, buf);
					break;
				default:
					handleUnexpectedServerPacket(ctx, type);
					break;
				}
			}catch(IndexOutOfBoundsException ex) {
				state = STATE_COMPLETED;
				ctx.close();
				logger().error("Failed to decode response from backend", ex);
			}
		}
		if(out.isEmpty()) {
			out.add(Unpooled.EMPTY_BUFFER); // :(
		}
	}

	private void kickClient(ChannelHandlerContext ctx) {
		ByteBuf packet = ctx.alloc().buffer();
		try {
			// Packet255KickDisconnect
			packet.writeByte(0xFF);
			BufferUtils.writeLegacyMCString(packet, "Internal Error", 256);
			ctx.writeAndFlush(packet.retain()).addListener(ChannelFutureListener.CLOSE);
		}finally {
			packet.release();
		}
	}

	private void handleServerVersion(ChannelHandlerContext ctx, ByteBuf buf) {
		if(state == STATE_SENT_HANDSHAKE) {
			state = STATE_STALLING;
			int protocolVers = buf.readUnsignedShort();
			int gameVers = buf.readUnsignedShort();
			if(protocolVers != 5 || gameVers != 47) {
				state = STATE_COMPLETED;
				kickClient(ctx);
				logger().error("Backend response does not match the requested protocol: V" + protocolVers + ", mc" + gameVers);
				return;
			}
			ByteBuf packet = ctx.alloc().buffer();
			try {
				// PROTOCOL_CLIENT_REQUEST_LOGIN
				packet.writeByte(0x04);
				packet.writeByte(username.length());
				BufferUtils.writeCharSequence(packet, username, StandardCharsets.US_ASCII);
				packet.writeByte(REWIND_STR.length);
				packet.writeBytes(REWIND_STR);
				packet.writeByte(0);
				packet.writeBoolean(false);
				packet.writeByte(0);
				BufferUtils.writeVarInt(packet, CAPABILITIES_MASK);
				for(int i = 0; i < CAPABILITIES_VER.length; ++i) {
					BufferUtils.writeVarInt(packet, CAPABILITIES_VER[i]);
				}
				packet.writeByte(0);
				state = STATE_SENT_REQUESTED_LOGIN;
				ctx.fireChannelRead(packet.retain());
			}finally {
				packet.release();
			}
		}else {
			handleUnexpectedServerPacket(ctx, 0x02);
		}
	}

	private void handleServerVersionMismatch(ChannelHandlerContext ctx, ByteBuf buf) {
		if(state == STATE_SENT_HANDSHAKE) {
			state = STATE_COMPLETED;
			kickClient(ctx);
			logger().error("Backend responded with PROTOCOL_VERISON_MISMATCH to requested protocol: V5, mc47");
		}else {
			handleUnexpectedServerPacket(ctx, 0x03);
		}
	}

	private void handleServerAllowLogin(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
		if(state == STATE_SENT_REQUESTED_LOGIN) {
			state = STATE_STALLING;
			
			int usernameLen = buf.readUnsignedByte();
			if(!charSeqEqual(BufferUtils.readCharSequence(buf, usernameLen, StandardCharsets.US_ASCII), username)) {
				state = STATE_COMPLETED;
				kickClient(ctx);
				logger().error("Backend assigned an unexpected username");
				return;
			}
			
			buf.skipBytes(16); // skip uuid
			
			ByteBuf packet = ctx.alloc().buffer();
			try {
				// Packet252SharedKey
				packet.writeByte(0xFC);
				packet.writeShort(0);
				packet.writeShort(0);
				out.add(packet.retain());
				state = STATE_SENT_RECEIVED_ALLOW_LOGIN;
			}finally {
				packet.release();
			}
		}else {
			handleUnexpectedServerPacket(ctx, 0x05);
		}
	}

	private boolean charSeqEqual(CharSequence seq1, String seq2) {
		int l = seq1.length();
		if(l != seq2.length()) {
			return false;
		}
		for(int i = 0; i < l; ++i) {
			if(seq1.charAt(i) != seq2.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	private void handleServerDenyLogin(ChannelHandlerContext ctx, ByteBuf buf) {
		if(state == STATE_SENT_REQUESTED_LOGIN) {
			state = STATE_STALLING;
			int len = buf.readUnsignedShort();
			String json = BufferUtils.readCharSequence(buf, len, StandardCharsets.UTF_8).toString();
			if(json.startsWith("{")) {
				try {
					json = serverAPI().getComponentHelper().convertJSONToLegacySection(json);
				}catch(Exception ex) {
				}
			}
			if(json.length() > 256) {
				json = json.substring(0, 256);
			}
			ByteBuf packet = ctx.alloc().buffer();
			try {
				// Packet255KickDisconnect
				packet.writeByte(0xFF);
				BufferUtils.writeLegacyMCString(packet, json, 256);
				state = STATE_COMPLETED;
				ctx.writeAndFlush(packet.retain()).addListener(ChannelFutureListener.CLOSE);
			}finally {
				packet.release();
			}
		}else {
			handleUnexpectedServerPacket(ctx, 0x06);
		}
	}

	private void handleServerFinishLogin(ChannelHandlerContext ctx, ByteBuf buf) {
		if(state == STATE_SENT_FINISH_LOGIN) {
			state = STATE_COMPLETED;
			enterPlayState();
		}else {
			handleUnexpectedServerPacket(ctx, 0x09);
		}
	}

	private static final byte[] LEGACY_REDIRECT;

	static {
		String str = "EAG|Reconnect";
		int len = str.length();
		ByteBuf buf = Unpooled.wrappedBuffer(LEGACY_REDIRECT = new byte[15 + len * 2]);
		buf.writerIndex(0);
		buf.writeByte(0x01);
		buf.writeInt(0);
		buf.writeShort(0);
		buf.writeByte(0);
		buf.writeByte(0);
		buf.writeByte(0xFF);
		buf.writeByte(0);
		buf.writeByte(0);
		buf.writeByte(0xFA);
		buf.writeShort(len);
		for(int i = 0; i < len; ++i) {
			buf.writeChar(str.charAt(i));
		}
	}

	private void handleServerRedirectTo(ChannelHandlerContext ctx, ByteBuf buf) {
		if(state == STATE_SENT_REQUESTED_LOGIN) {
			state = STATE_COMPLETED;
			int len = buf.readUnsignedShort();
			ByteBuf packet = ctx.alloc().buffer(LEGACY_REDIRECT.length + len + 2);
			try {
				packet.writeBytes(LEGACY_REDIRECT);
				packet.writeBytes(buf, buf.readerIndex() - 2, len + 2);
				ctx.writeAndFlush(packet.retain()).addListener(ChannelFutureListener.CLOSE);
			}finally {
				packet.release();
			}
		}else {
			handleUnexpectedServerPacket(ctx, 0x0A);
		}
	}

	private void handleServerError(ChannelHandlerContext ctx, ByteBuf buf) {
		state = STATE_COMPLETED;
		int errorCode = buf.readUnsignedByte();
		int stringLen = buf.readUnsignedShort();
		String str = BufferUtils.readCharSequence(buf, stringLen, StandardCharsets.UTF_8).toString();
		if(errorCode == 0x08) {
			// SERVER_ERROR_CUSTOM_MESSAGE
			String str2 = str;
			if(str2.startsWith("{")) {
				try {
					str2 = serverAPI().getComponentHelper().convertJSONToLegacySection(str2);
				}catch(Exception ex) {
				}
			}
			ByteBuf packet = ctx.alloc().buffer();
			try {
				// Packet255KickDisconnect
				packet.writeByte(0xFF);
				BufferUtils.writeLegacyMCString(packet, str2, 256);
				ctx.writeAndFlush(packet.retain()).addListener(ChannelFutureListener.CLOSE);
			}finally {
				packet.release();
			}
		}else {
			kickClient(ctx);
		}
		logger().error("Received error code " + errorCode + " from server: \"" + str + "\"");
	}

	private void handleUnexpectedClientPacket(ChannelHandlerContext ctx, int type) {
		int oldState = state;
		state = STATE_COMPLETED;
		kickClient(ctx);
		logger().error("Unexpected packet type " + type + " received from client in state " + oldState);
	}

	private void handleUnexpectedServerPacket(ChannelHandlerContext ctx, int type) {
		int oldState = state;
		state = STATE_COMPLETED;
		kickClient(ctx);
		logger().error("Unexpected packet type " + type + " received from server in state " + oldState);
	}

	private void enterPlayState() {
		handler().setEncoder(new RewindPacketEncoder<>(serverAPI().getHPPC()));
		handler().setDecoder(new RewindPacketDecoder<>());
	}

}
