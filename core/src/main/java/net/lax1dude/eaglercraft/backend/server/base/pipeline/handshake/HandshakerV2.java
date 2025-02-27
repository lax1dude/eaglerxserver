package net.lax1dude.eaglercraft.backend.server.base.pipeline.handshake;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketEaglerInitialHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class HandshakerV2 extends HandshakerV1 {

	public HandshakerV2(EaglerXServer<?> server, NettyPipelineData pipelineData,
			WebSocketEaglerInitialHandler inboundHandler) {
		super(server, pipelineData, inboundHandler);
	}

	public void init(ChannelHandlerContext ctx, String eaglerBrand, String eaglerVersionString) {
		throw new IllegalStateException();
	}

	public void init(ChannelHandlerContext ctx, String eaglerBrand, String eaglerVersionString,
			int minecraftVersion, boolean auth, byte[] authUsername) {
		handlePacketInit(ctx, eaglerBrand, eaglerVersionString, minecraftVersion, auth, authUsername);
	}

	@Override
	protected int getVersion() {
		return 2;
	}

	@Override
	protected GamePluginMessageProtocol getFinalVersion() {
		return GamePluginMessageProtocol.V3;
	}

	@Override
	protected ChannelFuture sendPacketAuthRequired(ChannelHandlerContext ctx,
			IEaglercraftAuthCheckRequiredEvent.EnumAuthType authMethod, String message) {
		return inboundHandler.sendErrorCode(ctx, getVersion(), HandshakePacketTypes.SERVER_ERROR_AUTHENTICATION_REQUIRED,
				HandshakePacketTypes.AUTHENTICATION_REQUIRED + " [" + getAuthTypeId(authMethod) + "] " + message);
	}

	@Override
	protected ChannelFuture sendPacketVersionAuth(ChannelHandlerContext ctx, int selectedEaglerProtocol,
			int selectedMinecraftProtocol, String serverBrand, String serverVersion,
			IEaglercraftAuthCheckRequiredEvent.EnumAuthType authMethod, byte[] authSaltingData) {
		int authMethId = getAuthTypeId(authMethod);
		
		if(authMethId == -1) {
			inboundHandler.terminateInternalError(ctx, getVersion());
			pipelineData.connectionLogger.error("Unsupported authentication method resolved: " + authMethod);
			return null;
		}
		
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

		buffer.writeByte(authMethId);
		if(authSaltingData != null) {
			buffer.writeShort(authSaltingData.length);
			buffer.writeBytes(authSaltingData);
		}else {
			buffer.writeShort(0);
		}
		
		return ctx.writeAndFlush(new BinaryWebSocketFrame(buffer));
	}

	protected int getAuthTypeId(IEaglercraftAuthCheckRequiredEvent.EnumAuthType meth) {
		switch(meth) {
		case PLAINTEXT:
			return 255; // plaintext authentication
		case EAGLER_SHA256:
			return 1; // eagler_sha256 authentication
		case AUTHME_SHA256:
			return 2; // authme_sha256 authentication
		default:
			return -1;
		}
	}

}
