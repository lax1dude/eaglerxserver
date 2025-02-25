package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings.ConfigDataProtocols;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.handshake.HandshakePacketTypes;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.handshake.HandshakerV1;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.handshake.HandshakerV2;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.handshake.HandshakerV3;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.handshake.HandshakerV4;

public class WebSocketInitialInboundHandler extends MessageToMessageCodec<WebSocketFrame, ByteBuf> {

	public interface IHandshaker {

		void handleInbound(ChannelHandlerContext ctx, ByteBuf buffer);

		boolean handleOutbound(ChannelHandlerContext ctx, ByteBuf buffer);

	}

	private final EaglerXServer<?> server;
	private final NettyPipelineData pipelineData;
	private List<ByteBuf> waitingOutboundFrames;
	private IHandshaker handshaker;
	private boolean terminated;

	public WebSocketInitialInboundHandler(EaglerXServer<?> server, NettyPipelineData pipelineData, List<ByteBuf> waitingOutboundFrames) {
		this.server = server;
		this.pipelineData = pipelineData;
		this.waitingOutboundFrames = waitingOutboundFrames;
	}

	public void setHandshaker(IHandshaker handshaker) {
		this.handshaker = handshaker;
	}

	public ChannelFuture sendErrorCode(ChannelHandlerContext ctx, int handshakeProtocol, int errorCode, String errorMessage) {
		ByteBuf buf = ctx.alloc().buffer();
		buf.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_ERROR);
		buf.writeByte(errorCode);
		if(handshakeProtocol >= 3) {
			if(errorMessage.length() > 65535) {
				errorMessage = errorMessage.substring(0, 65535);
			}
			byte[] msg = errorMessage.getBytes(StandardCharsets.UTF_8);
			buf.writeShort(msg.length);
			buf.writeBytes(msg);
		}else {
			if(errorMessage.length() > 255) {
				errorMessage = errorMessage.substring(0, 255);
			}
			byte[] msg = errorMessage.getBytes(StandardCharsets.UTF_8);
			buf.writeByte(msg.length);
			buf.writeBytes(msg);
		}
		return ctx.writeAndFlush(new BinaryWebSocketFrame(buf));
	}

	public void terminateErrorCode(ChannelHandlerContext ctx, int handshakeProtocol, int errorCode, String errorMessage) {
		terminated = true;
		sendErrorCode(ctx, handshakeProtocol, errorCode, errorMessage).addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> output) throws Exception {
		if (!terminated && ctx.channel().isActive()) {
			if(msg instanceof BinaryWebSocketFrame) {
				BinaryWebSocketFrame binaryMsg = (BinaryWebSocketFrame) msg;
				ByteBuf data = binaryMsg.content();
				if(handshaker != null) {
					handshaker.handleInbound(ctx, data);
				}else {
					if(data.readableBytes() > 2) {
						short b1 = data.readUnsignedByte();
						short b2 = data.readUnsignedByte();
						if(b1 == 1) {
							// EaglercraftX 1.8
							if(b2 == 2) {
								// EaglercraftX 1.8 v2/v3/v4/v5
								handleEaglerConnection(ctx, data);
								return;
							}else if(b2 == 1) {
								// EaglercraftX 1.8 v1
								handleEaglerLegacyConnection(ctx, data);
								return;
							}
						}else if(b1 == 2) {
							// Packet2ClientProtocol
							handleRewindConnection(ctx, b2, data);
							return;
						}
					}
					ctx.close();
				}
			}else if(msg instanceof TextWebSocketFrame) {
				if(handshaker == null) {
					server.getPipelineTransformer().removeVanillaHandlers(ctx.pipeline());
					//TODO: query
				}else {
					ctx.close();
				}
			}else if(msg instanceof CloseWebSocketFrame) {
				ctx.close();
			}
		}
	}

	private void handleEaglerConnection(ChannelHandlerContext ctx, ByteBuf binaryMsg) {
		boolean v2InList = false;
		boolean v3InList = false;
		boolean v4InList = false;
		int minClientProtocol = Integer.MAX_VALUE;
		int maxClientProtocol = Integer.MIN_VALUE;
		ConfigDataProtocols protocols = server.getConfig().getSettings().getProtocols();
		int minAllowedMC = protocols.getMinMinecraftProtocol();
		int maxAllowedMC = protocols.getMaxMinecraftProtocol();
		int minClientMC = Integer.MAX_VALUE;
		int maxClientMC = Integer.MIN_VALUE;
		int maxAvailableMC = Integer.MIN_VALUE;
		try {
			int cnt = binaryMsg.readUnsignedShort();
			if(cnt == 0 || cnt > 16) {
				throw new IndexOutOfBoundsException();
			}
			for(int i = 0; i < cnt; ++i) {
				int j = binaryMsg.readUnsignedShort();
				if(j > maxClientProtocol) {
					maxClientProtocol = j;
				}
				if(j < minClientProtocol) {
					minClientProtocol = j;
				}
				switch(j) {
				case 2:
					if(v2InList) {
						ctx.close();
						return;
					}
					v2InList = true;
					break;
				case 3:
					if(v3InList) {
						ctx.close();
						return;
					}
					v3InList = true;
					break;
				case 4:
					if(v4InList) {
						ctx.close();
						return;
					}
					v4InList = true;
					break;
				}
			}
			cnt = binaryMsg.readUnsignedShort();
			if(cnt == 0 || cnt > 16) {
				throw new IndexOutOfBoundsException();
			}
			for(int i = 0; i < cnt; ++i) {
				int j = binaryMsg.readUnsignedShort();
				if(j > maxClientMC) {
					maxClientMC = j;
				}
				if(j < minClientMC) {
					minClientMC = j;
				}
				if(j >= minAllowedMC && j <= maxAllowedMC) {
					if(j > maxAvailableMC) {
						maxAvailableMC = j;
					}
				}
			}
		}catch(IndexOutOfBoundsException ex) {
			ctx.close();
			return;
		}
		
		int selectedHandshakeProtocol = -1;
		boolean versionMismatch = maxAvailableMC == Integer.MIN_VALUE;
		boolean isServerProbablyOutdated = false;
		boolean isClientProbablyOutdated = false;
		if(!versionMismatch) {
			if(v4InList && protocols.isProtocolV4Allowed()) {
				selectedHandshakeProtocol = 4;
			}else if(v3InList && protocols.isProtocolV3Allowed()) {
				selectedHandshakeProtocol = 3;
			}else if(v2InList && protocols.isProtocolLegacyAllowed()) {
				selectedHandshakeProtocol = 2;
			}else {
				versionMismatch = true;
				isServerProbablyOutdated = minClientProtocol > protocols.getMaxEaglerProtocol()
						&& maxClientProtocol > protocols.getMaxEaglerProtocol();
				isClientProbablyOutdated = minClientProtocol < protocols.getMinEaglerProtocol()
						&& maxClientProtocol < protocols.getMinEaglerProtocol();
			}
		}else {
			isServerProbablyOutdated = minClientMC > maxAllowedMC && maxClientMC > maxAllowedMC;
			isClientProbablyOutdated = minClientMC < minAllowedMC && maxClientMC < minAllowedMC;
		}
		
		if(versionMismatch) {
			terminated = true;
			ByteBuf buf = ctx.alloc().buffer();
			buf.writeByte(HandshakePacketTypes.PROTOCOL_VERSION_MISMATCH);
			int cnt = 0;
			if(protocols.isProtocolLegacyAllowed()) {
				++cnt;
			}
			if(protocols.isProtocolV3Allowed()) {
				++cnt;
			}
			if(protocols.isProtocolV4Allowed()) {
				++cnt;
			}
			buf.writeShort(cnt);
			if(protocols.isProtocolLegacyAllowed()) {
				buf.writeShort(2);
			}
			if(protocols.isProtocolV3Allowed()) {
				buf.writeShort(3);
			}
			if(protocols.isProtocolV4Allowed()) {
				buf.writeShort(4);
			}
			buf.writeShort(2);
			buf.writeShort(minAllowedMC);
			buf.writeShort(maxAllowedMC);
			String str = isClientProbablyOutdated ? "Outdated Client" : (isServerProbablyOutdated ? "Outdated Server" : "Unsupported Client Version");
			buf.writeByte(str.length());
			buf.writeCharSequence(str, StandardCharsets.US_ASCII);
			ctx.writeAndFlush(new BinaryWebSocketFrame(buf)).addListener(ChannelFutureListener.CLOSE);
			return;
		}
		
		int strlen;
		String eaglerBrand, eaglerVersionString;
		boolean clientAuth;
		byte[] authUsername;
		try {
			strlen = binaryMsg.readUnsignedByte();
			eaglerBrand = binaryMsg.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
			strlen = binaryMsg.readUnsignedByte();
			eaglerVersionString = binaryMsg.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
			clientAuth = binaryMsg.readBoolean();
			strlen = binaryMsg.readUnsignedByte();
			authUsername = new byte[strlen];
			binaryMsg.readBytes(authUsername);
			if(binaryMsg.isReadable()) {
				throw new IndexOutOfBoundsException();
			}
		}catch(IndexOutOfBoundsException ex) {
			ctx.close();
			return;
		}
		
		if(selectedHandshakeProtocol == 4) {
			handshaker = (new HandshakerV4(this)).init(eaglerBrand, eaglerVersionString, maxAvailableMC, clientAuth, authUsername);
		}else if(selectedHandshakeProtocol == 3) {
			handshaker = (new HandshakerV3(this)).init(eaglerBrand, eaglerVersionString, maxAvailableMC, clientAuth, authUsername);
		}else if(selectedHandshakeProtocol == 2) {
			handshaker = (new HandshakerV2(this)).init(eaglerBrand, eaglerVersionString, maxAvailableMC, clientAuth, authUsername);
		}else {
			ctx.close();
			return;
		}
	}

	private void handleEaglerLegacyConnection(ChannelHandlerContext ctx, ByteBuf binaryMsg) {
		int strlen;
		String eaglerBrand, eaglerVersionString;
		try {
			strlen = binaryMsg.readUnsignedByte();
			eaglerBrand = binaryMsg.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
			strlen = binaryMsg.readUnsignedByte();
			eaglerVersionString = binaryMsg.readCharSequence(strlen, StandardCharsets.US_ASCII).toString();
			if(binaryMsg.isReadable()) {
				throw new IndexOutOfBoundsException();
			}
		}catch(IndexOutOfBoundsException ex) {
			ctx.close();
			return;
		}
		handshaker = (new HandshakerV1(this)).init(eaglerBrand, eaglerVersionString);
	}

	private void handleRewindConnection(ChannelHandlerContext ctx, int protocolVers, ByteBuf binaryMsg) {
		// TODO
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> output) throws Exception {
		if (!terminated && ctx.channel().isActive()) {
			if(waitingOutboundFrames == null) {
				waitingOutboundFrames = new ArrayList<>(4);
			}
			waitingOutboundFrames.add(msg.retain());
		}
		output.add(Unpooled.EMPTY_BUFFER); // :(
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		try {
			super.channelInactive(ctx);
		}finally {
			release();
		}
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		try {
			super.handlerRemoved(ctx);
		}finally {
			release();
		}
	}

	private void release() {
		if (waitingOutboundFrames != null) {
			for (ByteBuf b : waitingOutboundFrames) {
				b.release();
			}
			waitingOutboundFrames = null;
		}
	}

}
