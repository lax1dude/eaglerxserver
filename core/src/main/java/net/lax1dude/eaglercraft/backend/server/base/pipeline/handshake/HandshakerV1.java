package net.lax1dude.eaglercraft.backend.server.base.pipeline.handshake;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketEaglerInitialHandler;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketEaglerInitialHandler.IHandshaker;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class HandshakerV1 extends HandshakerInstance implements IHandshaker {

	public HandshakerV1(EaglerXServer<?> server, NettyPipelineData pipelineData,
			WebSocketEaglerInitialHandler inboundHandler) {
		super(server, pipelineData, inboundHandler);
	}

	public void init(ChannelHandlerContext ctx, String eaglerBrand, String eaglerVersionString) {
		handlePacketInit(ctx, eaglerBrand, eaglerVersionString, 47, false, null);
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
			handleInvalidData(ctx);
		}
	}

	protected void handleInboundRequestLogin(ChannelHandlerContext ctx, ByteBuf buffer) {
		int strlen = buffer.readUnsignedByte();
		String username = buffer.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
		strlen = buffer.readUnsignedByte();
		String requestedServer = buffer.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
		strlen = buffer.readUnsignedByte();
		byte[] authPassword = Util.newByteArray(strlen);
		buffer.readBytes(authPassword);
		if(buffer.isReadable()) {
			throw new IndexOutOfBoundsException();
		}
		handlePacketRequestLogin(ctx, username, requestedServer, authPassword, false, Util.ZERO_BYTES);
	}

	protected void handleInboundProfileData(ChannelHandlerContext ctx, ByteBuf buffer) {
		int strlen = buffer.readUnsignedByte();
		String type = buffer.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
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
		
		buffer.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_VERSION);
		buffer.writeByte(1);
		
		int len = serverBrand.length();
		if(len > 255) {
			serverBrand = serverBrand.substring(0, 255);
			len = 255;
		}
		buffer.writeByte(len);
		buffer.writeCharSequence(serverBrand, StandardCharsets.US_ASCII);
		
		len = serverVersion.length();
		if(len > 255) {
			serverVersion = serverVersion.substring(0, 255);
			len = 255;
		}
		buffer.writeByte(len);
		buffer.writeCharSequence(serverVersion, StandardCharsets.US_ASCII);
		
		buffer.writeByte(0);
		buffer.writeShort(0);
		
		return ctx.writeAndFlush(new BinaryWebSocketFrame(buffer));
	}

	@Override
	protected ChannelFuture sendPacketVersionAuth(ChannelHandlerContext ctx, int selectedEaglerProtocol,
			int selectedMinecraftProtocol, String serverBrand, String serverVersion,
			IEaglercraftAuthCheckRequiredEvent.EnumAuthType authMethod, byte[] authSaltingData) {
		throw new IllegalStateException();
	}

	@Override
	protected ChannelFuture sendPacketAllowLogin(ChannelHandlerContext ctx, String setUsername, UUID setUUID) {
		ByteBuf buffer = ctx.alloc().buffer();
		buffer.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_ALLOW_LOGIN);
		buffer.writeByte(setUsername.length());
		buffer.writeCharSequence(setUsername, StandardCharsets.US_ASCII);
		buffer.writeLong(setUUID.getMostSignificantBits());
		buffer.writeLong(setUUID.getLeastSignificantBits());
		return ctx.writeAndFlush(new BinaryWebSocketFrame(buffer));
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
		buffer.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_DENY_LOGIN);
		byte[] msg = message.getBytes(StandardCharsets.UTF_8);
		int len = msg.length;
		if(len > 255) {
			len = 255;
		}
		buffer.writeByte(len);
		buffer.writeBytes(msg, 0, len);
		return ctx.writeAndFlush(new BinaryWebSocketFrame(buffer));
	}

	@Override
	protected ChannelFuture sendPacketFinishLogin(ChannelHandlerContext ctx) {
		ByteBuf buffer = ctx.alloc().buffer();
		buffer.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_FINISH_LOGIN);
		return ctx.writeAndFlush(new BinaryWebSocketFrame(buffer));
	}

}
