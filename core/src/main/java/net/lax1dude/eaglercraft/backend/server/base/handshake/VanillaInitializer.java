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

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
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
			int port = 65535;
			if(ip == null) {
				ip = "127.0.0.1";
			}else {
				int i = ip.lastIndexOf(':');
				if(i != -1 && i < ip.length() - 1) {
					try {
						port = Integer.parseInt(ip.substring(i + 1));
						ip = ip.substring(0, i);
					}catch(NumberFormatException ex) {
					}
				}
				if(ip.length() > 255) {
					ip = ip.substring(0, 255);
				}
			}
			BufferUtils.writeMCString(buffer, ip, 255);
			buffer.writeShort(port);
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
		}else {
			inboundHandler.terminated = true;
		}

		return this;
	}

	public void handleInbound(ChannelHandlerContext ctx, ByteBuf msg) {
		try {
			int pktId = BufferUtils.readVarInt(msg, 3);
			if(pktId == 0x00) {
				// S00PacketDisconnect
				handleKickPacket(ctx, msg);
			}else {
				if(connectionState == STATE_SENT_LOGIN) {
					if(pktId == 0x02) {
						// S02PacketLoginSuccess
						UUID playerUUID;
						String uuidStr = BufferUtils.readMCString(msg, 36);
						try {
							playerUUID = UUID.fromString(uuidStr);
						}catch(IllegalArgumentException ex) {
							inboundHandler.terminateInternalError(ctx, pipelineData.handshakeProtocol);
							return;
						}
						String usernameStr = BufferUtils.readMCString(msg, 16);
						inboundHandler.handleBackendHandshakeSuccess(ctx, usernameStr, playerUUID);
					}else if(pktId == 0x03) {
						// S03PacketEnableCompression
					}else if(pktId == 0x01) {
						// S01PacketEncryptionRequest
						inboundHandler.terminateInternalError(ctx, pipelineData.handshakeProtocol);
						pipelineData.connectionLogger.error("Disconnecting, server tried to enable online mode encryption, please make sure the server is not in online mode");
					}else {
						inboundHandler.terminateInternalError(ctx, pipelineData.handshakeProtocol);
						pipelineData.connectionLogger.error("Disconnecting, server sent unknown packet " + pktId + " while handshaking");
					}
				}else {
					pipelineData.connectionLogger.error("Disconnecting, server sent unexpected packet " + pktId);
					inboundHandler.terminateInternalError(ctx, pipelineData.handshakeProtocol);
				}
			}
		}catch(IndexOutOfBoundsException ex) {
			ex.printStackTrace();
			inboundHandler.terminateInternalError(ctx, pipelineData.handshakeProtocol);
		}
	}

	private void handleKickPacket(ChannelHandlerContext ctx, ByteBuf data) {
		String pkt = BufferUtils.readMCString(data, 32767);
		inboundHandler.terminateErrorCode(ctx, pipelineData.handshakeProtocol,
				HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE, pkt);
		connectionState = STATE_COMPLETE;
	}

}
