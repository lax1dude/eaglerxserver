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

package net.lax1dude.eaglercraft.backend.server.base.handshake;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.BufferUtils;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketEaglerInitialHandler;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class HandshakerV5 extends HandshakerV4 {

	public HandshakerV5(EaglerXServer<?> server, NettyPipelineData pipelineData,
			WebSocketEaglerInitialHandler inboundHandler) {
		super(server, pipelineData, inboundHandler);
	}

	public void init(ChannelHandlerContext ctx, String eaglerBrand, String eaglerVersionString, int minecraftVersion,
			boolean auth, byte[] authUsername) {
		if (authUsername != null) {
			handlePacketInit(ctx, eaglerBrand, eaglerVersionString, minecraftVersion, auth, authUsername);
		} else {
			handleInvalidData(ctx);
		}
	}

	@Override
	protected int getVersion() {
		return 5;
	}

	@Override
	protected GamePluginMessageProtocol getFinalVersion() {
		return GamePluginMessageProtocol.V5;
	}

	@Override
	protected ChannelFuture sendPacketVersionNoAuth(ChannelHandlerContext ctx, int selectedEaglerProtocol,
			int selectedMinecraftProtocol, String serverBrand, String serverVersion) {
		ByteBuf buffer = ctx.alloc().buffer();
		try {
			buffer.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_VERSION);
			buffer.writeShort(selectedEaglerProtocol);
			buffer.writeShort(selectedMinecraftProtocol);

			int len = serverBrand.length();
			if (len > 255) {
				serverBrand = serverBrand.substring(0, 255);
				len = 255;
			}
			buffer.writeByte(len);
			BufferUtils.writeCharSequence(buffer, serverBrand, StandardCharsets.US_ASCII);

			len = serverVersion.length();
			if (len > 255) {
				serverVersion = serverVersion.substring(0, 255);
				len = 255;
			}
			buffer.writeByte(len);
			BufferUtils.writeCharSequence(buffer, serverVersion, StandardCharsets.US_ASCII);

			buffer.writeByte(0);
			buffer.writeShort(0);
			buffer.writeBoolean(false);

			return ctx.writeAndFlush(buffer.retain());
		} finally {
			buffer.release();
		}
	}

	@Override
	protected ChannelFuture sendPacketVersionAuth(ChannelHandlerContext ctx, int selectedEaglerProtocol,
			int selectedMinecraftProtocol, String serverBrand, String serverVersion, byte authMethod,
			byte[] authSaltingData, boolean nicknameSelection) {
		ByteBuf buffer = ctx.alloc().buffer();
		try {
			buffer.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_VERSION);
			buffer.writeShort(selectedEaglerProtocol);
			buffer.writeShort(selectedMinecraftProtocol);

			int len = serverBrand.length();
			if (len > 255) {
				serverBrand = serverBrand.substring(0, 255);
				len = 255;
			}
			buffer.writeByte(len);
			BufferUtils.writeCharSequence(buffer, serverBrand, StandardCharsets.US_ASCII);

			len = serverVersion.length();
			if (len > 255) {
				serverVersion = serverVersion.substring(0, 255);
				len = 255;
			}
			buffer.writeByte(len);
			BufferUtils.writeCharSequence(buffer, serverVersion, StandardCharsets.US_ASCII);

			buffer.writeByte(authMethod);
			if (authSaltingData != null) {
				buffer.writeShort(authSaltingData.length);
				buffer.writeBytes(authSaltingData);
			} else {
				buffer.writeShort(0);
			}

			buffer.writeBoolean(nicknameSelection);

			return ctx.writeAndFlush(buffer.retain());
		} finally {
			buffer.release();
		}
	}

	@Override
	protected void handleInboundRequestLogin(ChannelHandlerContext ctx, ByteBuf buffer) {
		int strlen = buffer.readUnsignedByte();
		String username = BufferUtils.readCharSequence(buffer, strlen, StandardCharsets.US_ASCII).toString();
		strlen = buffer.readUnsignedByte();
		String requestedServer = BufferUtils.readCharSequence(buffer, strlen, StandardCharsets.US_ASCII).toString();
		strlen = buffer.readUnsignedByte();
		byte[] authPassword = Util.newByteArray(strlen);
		buffer.readBytes(authPassword);
		boolean enableCookie = buffer.readBoolean();
		int cookieLen = buffer.readUnsignedByte();
		byte[] cookieData = Util.ZERO_BYTES;
		if (enableCookie) {
			cookieData = Util.newByteArray(cookieLen);
			buffer.readBytes(cookieData);
		} else {
			if (cookieLen > 0) {
				throw new IndexOutOfBoundsException();
			}
		}
		int standardCaps = BufferUtils.readVarInt(buffer, 5);
		int capCount = Integer.bitCount(standardCaps);
		int[] capSupport = new int[capCount];
		for (int i = 0; i < capCount; ++i) {
			capSupport[i] = BufferUtils.readVarInt(buffer, 5);
		}
		UUID[] extendedCapUUIDs;
		int[] extendedCapVers;
		int extCapabilityCount = buffer.readUnsignedByte();
		if (extCapabilityCount > 0) {
			if (extCapabilityCount > 32) {
				throw new IndexOutOfBoundsException();
			}
			extendedCapUUIDs = new UUID[extCapabilityCount];
			extendedCapVers = new int[extCapabilityCount];
			for (int i = 0; i < extCapabilityCount; ++i) {
				extendedCapUUIDs[i] = new UUID(buffer.readLong(), buffer.readLong());
				extendedCapVers[i] = BufferUtils.readVarInt(buffer, 5);
			}
		} else {
			extendedCapUUIDs = NO_UUID;
			extendedCapVers = NO_VER;
		}
		if (buffer.isReadable()) {
			throw new IndexOutOfBoundsException();
		}
		handlePacketRequestLogin(ctx, username, requestedServer, authPassword, enableCookie, cookieData, standardCaps,
				capSupport, extendedCapUUIDs, extendedCapVers);
	}

	@Override
	protected ChannelFuture sendPacketAllowLogin(ChannelHandlerContext ctx, String setUsername, UUID setUUID,
			int standardCapabilities, byte[] standardCapabilityVersions, Map<UUID, Byte> extendedCapabilities) {
		ByteBuf buffer = ctx.alloc().buffer();
		try {
			buffer.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_ALLOW_LOGIN);
			buffer.writeByte(setUsername.length());
			BufferUtils.writeCharSequence(buffer, setUsername, StandardCharsets.US_ASCII);
			buffer.writeLong(setUUID.getMostSignificantBits());
			buffer.writeLong(setUUID.getLeastSignificantBits());
			BufferUtils.writeVarInt(buffer, standardCapabilities);
			buffer.writeBytes(standardCapabilityVersions);
			buffer.writeByte(extendedCapabilities.size());
			if (extendedCapabilities.size() > 0) {
				for (Map.Entry<UUID, Byte> etr : extendedCapabilities.entrySet()) {
					UUID uuid = etr.getKey();
					buffer.writeLong(uuid.getMostSignificantBits());
					buffer.writeLong(uuid.getLeastSignificantBits());
					buffer.writeByte(etr.getValue());
				}
			}
			return ctx.writeAndFlush(buffer.retain());
		} finally {
			buffer.release();
		}
	}

	@Override
	protected ChannelFuture sendPacketLoginStateRedirect(ChannelHandlerContext ctx, String address) {
		ByteBuf buffer = ctx.alloc().buffer();
		try {
			buffer.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_REDIRECT_TO);
			byte[] addr = address.getBytes(StandardCharsets.UTF_8);
			int len = addr.length;
			if (len > 65535) {
				len = 65535;
			}
			buffer.writeShort(len);
			buffer.writeBytes(addr, 0, len);
			return ctx.writeAndFlush(buffer.retain());
		} finally {
			buffer.release();
		}
	}

}
