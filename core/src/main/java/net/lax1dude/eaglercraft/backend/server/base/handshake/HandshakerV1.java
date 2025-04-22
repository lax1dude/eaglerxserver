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
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.BufferUtils;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketEaglerInitialHandler;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class HandshakerV1 extends HandshakerInstance {

	public HandshakerV1(EaglerXServer<?> server, NettyPipelineData pipelineData,
			WebSocketEaglerInitialHandler inboundHandler) {
		super(server, pipelineData, inboundHandler);
	}

	public void init(ChannelHandlerContext ctx, int minecraftProtocol, String eaglerBrand, String eaglerVersionString) {
		handlePacketInit(ctx, eaglerBrand, eaglerVersionString, minecraftProtocol, false, null);
	}

	@Override
	public void handleInbound(ChannelHandlerContext ctx, ByteBuf buffer) {
		try {
			int packetId = buffer.readUnsignedByte();
			switch(packetId) {
			case HandshakePacketTypes.PROTOCOL_CLIENT_REQUEST_LOGIN:
				handleInboundRequestLogin(ctx, buffer);
				break;
			case HandshakePacketTypes.PROTOCOL_CLIENT_PROFILE_DATA:
				handleInboundProfileData(ctx, buffer);
				break;
			case HandshakePacketTypes.PROTOCOL_CLIENT_FINISH_LOGIN:
				handleInboundFinishLogin(ctx, buffer);
				break;
			default:
				handleUnknownPacket(ctx, packetId);
				break;
			}
		}catch(IndexOutOfBoundsException ex) {
			ex.printStackTrace();
			handleInvalidData(ctx);
		}catch(Throwable ex) {
			ex.printStackTrace();
		}
	}

	protected void handleInboundRequestLogin(ChannelHandlerContext ctx, ByteBuf buffer) {
		int strlen = buffer.readUnsignedByte();
		String username = BufferUtils.readCharSequence(buffer, strlen, StandardCharsets.US_ASCII).toString();
		strlen = buffer.readUnsignedByte();
		String requestedServer = BufferUtils.readCharSequence(buffer, strlen, StandardCharsets.US_ASCII).toString();
		strlen = buffer.readUnsignedByte();
		byte[] authPassword = Util.newByteArray(strlen);
		buffer.readBytes(authPassword);
		if(buffer.isReadable()) {
			throw new IndexOutOfBoundsException();
		}
		handlePacketRequestLogin(ctx, username, requestedServer, authPassword, false, Util.ZERO_BYTES,
				fallbackCapabilityMask(), fallbackCapabilityVers());
	}

	protected int fallbackCapabilityMask() {
		return 0;
	}

	protected int[] fallbackCapabilityVers() {
		return NO_VER;
	}

	protected void handleInboundProfileData(ChannelHandlerContext ctx, ByteBuf buffer) {
		int strlen = buffer.readUnsignedByte();
		String type = BufferUtils.readCharSequence(buffer, strlen, StandardCharsets.US_ASCII).toString();
		strlen = buffer.readUnsignedShort();
		byte[] readData = Util.newByteArray(strlen);
		buffer.readBytes(readData);
		handlePacketProfileData(ctx, type, readData);
	}

	protected void handleInboundFinishLogin(ChannelHandlerContext ctx, ByteBuf buffer) {
		handlePacketFinishLogin(ctx);
	}

	@Override
	protected int getVersion() {
		return 1;
	}

	@Override
	protected GamePluginMessageProtocol getFinalVersion() {
		return GamePluginMessageProtocol.V3;
	}

	@Override
	protected ChannelFuture sendPacketAuthRequired(ChannelHandlerContext ctx,
			IEaglercraftAuthCheckRequiredEvent.EnumAuthType authMethod, String message) {
		throw new IllegalStateException();
	}

	@Override
	protected ChannelFuture sendPacketVersionNoAuth(ChannelHandlerContext ctx, int selectedEaglerProtocol,
			int selectedMinecraftProtocol, String serverBrand, String serverVersion) {
		ByteBuf buffer = ctx.alloc().buffer();
		try {
			buffer.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_VERSION);
			buffer.writeByte(1);
			
			int len = serverBrand.length();
			if(len > 255) {
				serverBrand = serverBrand.substring(0, 255);
				len = 255;
			}
			buffer.writeByte(len);
			BufferUtils.writeCharSequence(buffer, serverBrand, StandardCharsets.US_ASCII);
			
			len = serverVersion.length();
			if(len > 255) {
				serverVersion = serverVersion.substring(0, 255);
				len = 255;
			}
			buffer.writeByte(len);
			BufferUtils.writeCharSequence(buffer, serverVersion, StandardCharsets.US_ASCII);
			
			buffer.writeByte(0);
			buffer.writeShort(0);
			
			return ctx.writeAndFlush(buffer.retain());
		}finally {
			buffer.release();
		}
	}

	@Override
	protected ChannelFuture sendPacketVersionAuth(ChannelHandlerContext ctx, int selectedEaglerProtocol,
			int selectedMinecraftProtocol, String serverBrand, String serverVersion,
			IEaglercraftAuthCheckRequiredEvent.EnumAuthType authMethod, byte[] authSaltingData, boolean nicknameSelection) {
		throw new IllegalStateException();
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
			return ctx.writeAndFlush(buffer.retain());
		}finally {
			buffer.release();
		}
	}

	@Override
	protected ChannelFuture sendPacketDenyLogin(ChannelHandlerContext ctx, Object component) {
		return sendPacketDenyLogin(ctx, server.componentHelper().serializeLegacySection(component));
	}

	@Override
	protected ChannelFuture sendPacketDenyLogin(ChannelHandlerContext ctx, String message) {
		if(message.length() > 255) {
			message = message.substring(0, 255);
		}
		ByteBuf buffer = ctx.alloc().buffer();
		try {
			buffer.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_DENY_LOGIN);
			byte[] msg = message.getBytes(StandardCharsets.UTF_8);
			int len = msg.length;
			if(len > 255) {
				len = 255;
			}
			buffer.writeByte(len);
			buffer.writeBytes(msg, 0, len);
			return ctx.writeAndFlush(buffer.retain());
		}finally {
			buffer.release();
		}
	}

	@Override
	protected ChannelFuture sendPacketFinishLogin(ChannelHandlerContext ctx) {
		ByteBuf buffer = ctx.alloc().buffer();
		try {
			buffer.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_FINISH_LOGIN);
			return ctx.writeAndFlush(buffer.retain());
		}finally {
			buffer.release();
		}
	}

	@Override
	protected ChannelFuture sendPacketLoginStateRedirect(ChannelHandlerContext ctx, String address) {
		throw new UnsupportedOperationException();
	}

}
