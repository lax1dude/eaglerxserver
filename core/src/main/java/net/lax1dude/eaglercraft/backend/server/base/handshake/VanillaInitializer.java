package net.lax1dude.eaglercraft.backend.server.base.handshake;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.server.api.EnumPipelineEvent;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.BufferUtils;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketEaglerInitialHandler;

public class VanillaInitializer {

	protected final EaglerXServer<?> server;
	protected final NettyPipelineData pipelineData;
	protected final WebSocketEaglerInitialHandler inboundHandler;

	private static final int STATE_PRE = 0;
	private static final int STATE_SENT_LOGIN = 1;
	private static final int STATE_COMPLETE = 2;
	private int connectionState = STATE_PRE;

	public VanillaInitializer(EaglerXServer<?> server, NettyPipelineData pipelineData,
			WebSocketEaglerInitialHandler inboundHandler) {
		this.server = server;
		this.pipelineData = pipelineData;
		this.inboundHandler = inboundHandler;
	}

	public VanillaInitializer init(ChannelHandlerContext ctx) {		
		// C00Handshake
		ByteBuf buffer = ctx.alloc().buffer();
		try {
			BufferUtils.writeVarInt(buffer, 0x00);
			BufferUtils.writeVarInt(buffer, pipelineData.minecraftProtocol);
			String ip = pipelineData.headerHost;
			if(ip == null) {
				ip = "127.0.0.1";
			}
			BufferUtils.writeMCString(buffer, ip, 255);
			buffer.writeShort(8080);
			BufferUtils.writeVarInt(buffer, 2);
			ctx.fireChannelRead(buffer.retain());
		}finally {
			buffer.release();
		}
		
		// C00PacketLoginStart
		buffer = ctx.alloc().buffer();
		try {
			BufferUtils.writeVarInt(buffer, 0x00);
			BufferUtils.writeMCString(buffer, pipelineData.username, 16);
			ctx.fireChannelRead(buffer.retain());
		}finally {
			buffer.release();
		}

		if(ctx.channel().isActive()) {
			connectionState = STATE_SENT_LOGIN;
			server.getPlatform().handleConnectionInitFallback(ctx.channel());
		}else {
			inboundHandler.terminated = true;
		}

		return this;
	}

	public void handleInbound(ChannelHandlerContext ctx, ByteBuf msg) {
		try {
			int pktId = BufferUtils.readVarInt(msg, 3);
			if(connectionState == STATE_SENT_LOGIN) {
				if(pktId == 0x02) {
					// S02PacketLoginSuccess
					UUID playerUUID = new UUID(msg.readLong(), msg.readLong());
					if(!playerUUID.equals(pipelineData.uuid)) {
						inboundHandler.terminateInternalError(ctx, pipelineData.handshakeProtocol);
						pipelineData.connectionLogger.error("Disconnecting, platform assigned UUID to client that does not match the UUID the EaglerXServer sent to the client during the handshake");
					}else {
						String playerUsername = BufferUtils.readMCString(msg, 16);
						if(!playerUsername.equals(pipelineData.username)) {
							inboundHandler.terminateInternalError(ctx, pipelineData.handshakeProtocol);
							pipelineData.connectionLogger.error("Disconnecting, platform assigned username to client that does not match the username the EaglerXServer sent to the client during the handshake");
						}else {
							finish(ctx);
						}
					}
				}else if(pktId == 0x03) {
					// S03PacketEnableCompression
					int val = BufferUtils.readVarInt(msg, 5);
					if(val > 0) {
						server.getPlatform().handleUndoCompression(ctx);
						ctx.pipeline().fireUserEventTriggered(EnumPipelineEvent.EAGLER_DISABLE_COMPRESSION_HACK);
					}
				}else if(pktId == 0x01) {
					// S01PacketEncryptionRequest
					inboundHandler.terminateInternalError(ctx, pipelineData.handshakeProtocol);
					pipelineData.connectionLogger.error("Disconnecting, server tried to enable online mode encryption, please make sure the server is not in online mode");
				}else if(pktId == 0x00) {
					// S00PacketDisconnect
					handleKickPacket(ctx, msg);
				}else {
					inboundHandler.terminateInternalError(ctx, pipelineData.handshakeProtocol);
					pipelineData.connectionLogger.error("Disconnecting, server sent unknown packet " + pktId + " while handshaking");
				}
			}else {
				pipelineData.connectionLogger.error("Disconnecting, server sent unexpected packet");
				inboundHandler.terminateInternalError(ctx, pipelineData.handshakeProtocol);
			}
		}catch(IndexOutOfBoundsException ex) {
			inboundHandler.terminateInternalError(ctx, pipelineData.handshakeProtocol);
		}
	}

	private void handleKickPacket(ChannelHandlerContext ctx, ByteBuf data) {
		//Note: Very old clients (protocol v2, from 2022) will not handle this JSON correctly
		inboundHandler.terminateErrorCode(ctx, pipelineData.handshakeProtocol,
				HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, BufferUtils.readMCString(data, 65535));
		connectionState = STATE_COMPLETE;
	}

	private void finish(ChannelHandlerContext ctx) {
		inboundHandler.enterPlayState(ctx);
	}

}
