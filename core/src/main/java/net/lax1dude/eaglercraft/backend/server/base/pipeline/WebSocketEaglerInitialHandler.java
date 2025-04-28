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

package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageCodec;
import net.lax1dude.eaglercraft.backend.server.api.EnumPipelineEvent;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindProtocol;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.RewindInitializer;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSettings.ConfigDataProtocols;
import net.lax1dude.eaglercraft.backend.server.base.handshake.HandshakePacketTypes;
import net.lax1dude.eaglercraft.backend.server.base.handshake.HandshakerInstance;
import net.lax1dude.eaglercraft.backend.server.base.handshake.HandshakerV1;
import net.lax1dude.eaglercraft.backend.server.base.handshake.HandshakerV2;
import net.lax1dude.eaglercraft.backend.server.base.handshake.HandshakerV3;
import net.lax1dude.eaglercraft.backend.server.base.handshake.HandshakerV4;
import net.lax1dude.eaglercraft.backend.server.base.handshake.HandshakerV5;
import net.lax1dude.eaglercraft.backend.server.base.handshake.VanillaInitializer;
import net.lax1dude.eaglercraft.backend.server.base.message.RewindMessageInjector;
import net.lax1dude.eaglercraft.backend.server.util.Util;

public class WebSocketEaglerInitialHandler extends MessageToMessageCodec<ByteBuf, ByteBuf> {

	public interface IHandshaker {

		void handleInbound(ChannelHandlerContext ctx, ByteBuf buffer);

		void handleBackendHandshakeSuccess(ChannelHandlerContext ctx, String acceptedUsername, UUID acceptedUUID);

		void finish(ChannelHandlerContext ctx);

	}

	private static final byte[] LEGACY_KICK;
	private static final byte[] LEGACY_REDIRECT;

	static {
		String str = "Outdated Client";
		int len = str.length();
		ByteBuf buf = Unpooled.wrappedBuffer(LEGACY_KICK = new byte[3 + len * 2]);
		buf.writerIndex(0);
		buf.writeByte(0xFF);
		buf.writeShort(len);
		for (int i = 0; i < len; ++i) {
			buf.writeChar(str.charAt(i));
		}
		str = "EAG|Reconnect";
		len = str.length();
		buf = Unpooled.wrappedBuffer(LEGACY_REDIRECT = new byte[15 + len * 2]);
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
		for (int i = 0; i < len; ++i) {
			buf.writeChar(str.charAt(i));
		}
	}

	private final EaglerXServer<?> server;
	private final NettyPipelineData pipelineData;
	private IHandshaker handshaker;
	private VanillaInitializer vanillaInitializer;
	public boolean terminated;

	public WebSocketEaglerInitialHandler(EaglerXServer<?> server, NettyPipelineData pipelineData) {
		this.server = server;
		this.pipelineData = pipelineData;
	}

	public ChannelFuture sendErrorCode(ChannelHandlerContext ctx, int handshakeProtocol, int errorCode,
			String errorMessage) {
		ByteBuf buf = ctx.alloc().buffer();
		buf.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_ERROR);
		buf.writeByte(errorCode);
		if (handshakeProtocol >= 3) {
			if (errorMessage.length() > 65535) {
				errorMessage = errorMessage.substring(0, 65535);
			}
			byte[] msg = errorMessage.getBytes(StandardCharsets.UTF_8);
			buf.writeShort(msg.length);
			buf.writeBytes(msg);
		} else {
			if (errorMessage.startsWith("{")) {
				try {
					errorMessage = server.getComponentHelper().convertJSONToLegacySection(errorMessage);
				} catch (Exception ex) {
				}
			}
			if (errorMessage.length() > 255) {
				errorMessage = errorMessage.substring(0, 255);
			}
			byte[] msg = errorMessage.getBytes(StandardCharsets.UTF_8);
			buf.writeByte(msg.length);
			buf.writeBytes(msg);
		}
		return ctx.writeAndFlush(buf);
	}

	public ChannelFuture sendErrorCode(ChannelHandlerContext ctx, int handshakeProtocol, int errorCode,
			Object errorComponent) {
		ByteBuf buf = ctx.alloc().buffer();
		buf.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_ERROR);
		buf.writeByte(errorCode);
		if (handshakeProtocol >= 3) {
			String errorMessage = server.componentHelper().serializeLegacyJSON(errorComponent);
			if (errorMessage.length() > 65535) {
				errorMessage = errorMessage.substring(0, 65535);
			}
			byte[] msg = errorMessage.getBytes(StandardCharsets.UTF_8);
			int len = msg.length;
			if (len > 65535) {
				len = 65535;
			}
			buf.writeShort(len);
			buf.writeBytes(msg, 0, len);
		} else {
			String errorMessage = server.componentHelper().serializeLegacySection(errorComponent);
			if (errorMessage.length() > 255) {
				errorMessage = errorMessage.substring(0, 255);
			}
			byte[] msg = errorMessage.getBytes(StandardCharsets.UTF_8);
			int len = msg.length;
			if (len > 255) {
				len = 255;
			}
			buf.writeByte(len);
			buf.writeBytes(msg, 0, len);
		}
		return ctx.writeAndFlush(buf);
	}

	public void terminateInternalError(ChannelHandlerContext ctx, int handshakeProtocol) {
		terminateErrorCode(ctx, handshakeProtocol, HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE,
				HandshakePacketTypes.MSG_INTERNAL_ERROR);
	}

	public void terminateErrorCode(ChannelHandlerContext ctx, int handshakeProtocol, int errorCode,
			String errorMessage) {
		terminated = true;
		sendErrorCode(ctx, handshakeProtocol, errorCode, errorMessage).addListener(ChannelFutureListener.CLOSE);
	}

	public void terminateErrorCode(ChannelHandlerContext ctx, int handshakeProtocol, int errorCode,
			Object errorComponent) {
		terminated = true;
		sendErrorCode(ctx, handshakeProtocol, errorCode, errorComponent).addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf data, List<Object> output) throws Exception {
		if (!terminated && ctx.channel().isActive()) {
			if (handshaker != null) {
				handshaker.handleInbound(ctx, data);
			} else {
				if (data.readableBytes() > 2) {
					short b1 = data.readUnsignedByte();
					short b2 = data.readUnsignedByte();
					if (b1 == 1) {
						// EaglercraftX 1.8
						if (b2 == 2) {
							// EaglercraftX 1.8 v2/v3/v4/v5
							handleEaglerConnection(ctx, data);
							return;
						} else if (b2 == 1) {
							// EaglercraftX 1.8 v1
							handleEaglerLegacyConnection(ctx, data);
							return;
						}
					} else if (b1 == 2) {
						// Packet2ClientProtocol
						handleRewindConnection(ctx, b2, data);
						return;
					}
				}
				ctx.close();
			}
		}
	}

	private void handleEaglerConnection(ChannelHandlerContext ctx, ByteBuf binaryMsg) {
		boolean v2InList = false;
		boolean v3InList = false;
		boolean v4InList = false;
		boolean v5InList = false;
		int minClientProtocol = Integer.MAX_VALUE;
		int maxClientProtocol = Integer.MIN_VALUE;
		ConfigDataProtocols protocols = server.getConfig().getSettings().getProtocols();
		int minAllowedMC = protocols.getMinMinecraftProtocol();
		int maxAllowedMC = protocols.getMaxMinecraftProtocol();
		int maxAllowedMCV5 = protocols.getMaxMinecraftProtocolV5();
		int minClientMC = Integer.MAX_VALUE;
		int maxClientMC = Integer.MIN_VALUE;
		int maxAvailableMC = Integer.MIN_VALUE;
		try {
			int cnt = binaryMsg.readUnsignedShort();
			if (cnt == 0 || cnt > 16) {
				throw new IndexOutOfBoundsException();
			}
			for (int i = 0; i < cnt; ++i) {
				int j = binaryMsg.readUnsignedShort();
				if (j > maxClientProtocol) {
					maxClientProtocol = j;
				}
				if (j < minClientProtocol) {
					minClientProtocol = j;
				}
				switch (j) {
				case 2:
					if (v2InList) {
						ctx.close();
						return;
					}
					v2InList = true;
					break;
				case 3:
					if (v3InList) {
						ctx.close();
						return;
					}
					v3InList = true;
					break;
				case 4:
					if (v4InList) {
						ctx.close();
						return;
					}
					v4InList = true;
					break;
				case 5:
					if (v5InList) {
						ctx.close();
						return;
					}
					v5InList = true;
					maxAllowedMC = maxAllowedMCV5;
					break;
				}
			}
			if (maxAllowedMC == -1) {
				maxAllowedMC = Integer.MAX_VALUE;
			}
			cnt = binaryMsg.readUnsignedShort();
			if (cnt == 0 || cnt > 16) {
				throw new IndexOutOfBoundsException();
			}
			for (int i = 0; i < cnt; ++i) {
				int j = binaryMsg.readUnsignedShort();
				if (j > maxClientMC) {
					maxClientMC = j;
				}
				if (j < minClientMC) {
					minClientMC = j;
				}
				if (j >= minAllowedMC && j <= maxAllowedMC) {
					if (j > maxAvailableMC) {
						maxAvailableMC = j;
					}
				}
			}
		} catch (IndexOutOfBoundsException ex) {
			ctx.close();
			return;
		}

		int selectedHandshakeProtocol = -1;
		boolean versionMismatch = maxAvailableMC == Integer.MIN_VALUE;
		boolean isServerProbablyOutdated = false;
		boolean isClientProbablyOutdated = false;
		if (!versionMismatch) {
			if (v5InList && protocols.isProtocolV5Allowed()) {
				selectedHandshakeProtocol = 5;
			} else if (v4InList && protocols.isProtocolV4Allowed()) {
				selectedHandshakeProtocol = 4;
			} else if (v3InList && protocols.isProtocolV3Allowed()) {
				selectedHandshakeProtocol = 3;
			} else if (v2InList && protocols.isProtocolLegacyAllowed()) {
				selectedHandshakeProtocol = 2;
			} else {
				versionMismatch = true;
				isServerProbablyOutdated = minClientProtocol > protocols.getMaxEaglerProtocol()
						&& maxClientProtocol > protocols.getMaxEaglerProtocol();
				isClientProbablyOutdated = minClientProtocol < protocols.getMinEaglerProtocol()
						&& maxClientProtocol < protocols.getMinEaglerProtocol();
			}
		} else {
			isServerProbablyOutdated = minClientMC > maxAllowedMC && maxClientMC > maxAllowedMC;
			isClientProbablyOutdated = minClientMC < minAllowedMC && maxClientMC < minAllowedMC;
		}

		if (versionMismatch) {
			terminated = true;
			ByteBuf buf = ctx.alloc().buffer();
			buf.writeByte(HandshakePacketTypes.PROTOCOL_VERSION_MISMATCH);
			int cnt = 0;
			if (protocols.isProtocolLegacyAllowed()) {
				++cnt;
			}
			if (protocols.isProtocolV3Allowed()) {
				++cnt;
			}
			if (protocols.isProtocolV4Allowed()) {
				++cnt;
			}
			if (protocols.isProtocolV5Allowed()) {
				++cnt;
			}
			buf.writeShort(cnt);
			if (protocols.isProtocolLegacyAllowed()) {
				buf.writeShort(2);
			}
			if (protocols.isProtocolV3Allowed()) {
				buf.writeShort(3);
			}
			if (protocols.isProtocolV4Allowed()) {
				buf.writeShort(4);
			}
			if (protocols.isProtocolV5Allowed()) {
				buf.writeShort(5);
			}
			buf.writeShort(2);
			buf.writeShort(minAllowedMC);
			buf.writeShort(maxAllowedMC);
			String str = isClientProbablyOutdated ? "Outdated Client"
					: (isServerProbablyOutdated ? "Outdated Server" : "Unsupported Client Version");
			buf.writeByte(str.length());
			BufferUtils.writeCharSequence(buf, str, StandardCharsets.US_ASCII);
			ctx.writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE);
			return;
		}

		int strlen;
		String eaglerBrand, eaglerVersionString;
		boolean clientAuth;
		byte[] authUsername;
		try {
			strlen = binaryMsg.readUnsignedByte();
			eaglerBrand = BufferUtils.readCharSequence(binaryMsg, strlen, StandardCharsets.US_ASCII).toString();
			strlen = binaryMsg.readUnsignedByte();
			eaglerVersionString = BufferUtils.readCharSequence(binaryMsg, strlen, StandardCharsets.US_ASCII).toString();
			clientAuth = binaryMsg.readBoolean();
			strlen = binaryMsg.readUnsignedByte();
			authUsername = Util.newByteArray(strlen);
			binaryMsg.readBytes(authUsername);
			if (binaryMsg.isReadable()) {
				throw new IndexOutOfBoundsException();
			}
		} catch (IndexOutOfBoundsException ex) {
			ctx.close();
			return;
		}

		if (selectedHandshakeProtocol == 5) {
			HandshakerV5 hs = new HandshakerV5(server, pipelineData, this);
			handshaker = hs;
			hs.init(ctx, eaglerBrand, eaglerVersionString, maxAvailableMC, clientAuth, authUsername);
		} else if (selectedHandshakeProtocol == 4) {
			HandshakerV4 hs = new HandshakerV4(server, pipelineData, this);
			handshaker = hs;
			hs.init(ctx, eaglerBrand, eaglerVersionString, maxAvailableMC, clientAuth, authUsername);
		} else if (selectedHandshakeProtocol == 3) {
			HandshakerV3 hs = new HandshakerV3(server, pipelineData, this);
			handshaker = hs;
			hs.init(ctx, eaglerBrand, eaglerVersionString, maxAvailableMC, clientAuth, authUsername);
		} else if (selectedHandshakeProtocol == 2) {
			HandshakerV2 hs = new HandshakerV2(server, pipelineData, this);
			handshaker = hs;
			hs.init(ctx, eaglerBrand, eaglerVersionString, maxAvailableMC, clientAuth, authUsername);
		} else {
			ctx.close();
			return;
		}
	}

	private void handleEaglerLegacyConnection(ChannelHandlerContext ctx, ByteBuf binaryMsg) {
		int minecraftProtocol = binaryMsg.readUnsignedByte();
		int strlen;
		String eaglerBrand, eaglerVersionString;
		try {
			strlen = binaryMsg.readUnsignedByte();
			eaglerBrand = BufferUtils.readCharSequence(binaryMsg, strlen, StandardCharsets.US_ASCII).toString();
			strlen = binaryMsg.readUnsignedByte();
			eaglerVersionString = BufferUtils.readCharSequence(binaryMsg, strlen, StandardCharsets.US_ASCII).toString();
			if (binaryMsg.isReadable()) {
				throw new IndexOutOfBoundsException();
			}
		} catch (IndexOutOfBoundsException ex) {
			ctx.close();
			return;
		}
		HandshakerV1 hs = new HandshakerV1(server, pipelineData, this);
		handshaker = hs;
		hs.init(ctx, minecraftProtocol, eaglerBrand, eaglerVersionString);
	}

	private void handleRewindConnection(ChannelHandlerContext ctx, int protocolVers, ByteBuf binaryMsg) {
		ConfigDataProtocols protocols = server.getConfig().getSettings().getProtocols();
		if (!protocols.isEaglerXRewindAllowed()) {
			kickLegacy(ctx, protocolVers);
			return;
		}
		String username;
		String serverHost;
		int serverPort;
		try {
			username = BufferUtils.readLegacyMCString(binaryMsg, 16);
			serverHost = BufferUtils.readLegacyMCString(binaryMsg, 255);
			serverPort = binaryMsg.readInt();
		} catch (IndexOutOfBoundsException ex) {
			ctx.close();
			return;
		}
		IEaglerXRewindProtocol<?, ?> protocol = server.getPipelineTransformer().rewind.getProtocol(protocolVers);
		if (protocol == null) {
			kickLegacy(ctx, protocolVers);
			return;
		}
		if (!HandshakerInstance.USERNAME_REGEX.matcher(username).matches()) {
			ByteBuf legacyKickPacket = ctx.alloc().buffer();
			try {
				String msg = "Invalid Username";
				int len = msg.length();
				legacyKickPacket.writeByte(0xFF);
				legacyKickPacket.writeShort(len);
				for (int i = 0; i < len; ++i) {
					legacyKickPacket.writeChar(msg.charAt(i));
				}
				ctx.writeAndFlush(legacyKickPacket.retain()).addListener(ChannelFutureListener.CLOSE);
				return;
			} finally {
				legacyKickPacket.release();
			}
		}

		RewindInitializer<Object> initializer = new RewindInitializer<Object>(ctx.channel(), pipelineData, protocolVers,
				username, serverHost, serverPort) {

			@Override
			public void injectNettyHandlers0(ChannelOutboundHandler nettyEncoder, ChannelInboundHandler nettyDecoder) {
				ChannelPipeline pipeline = ctx.pipeline();
				pipeline.addBefore(PipelineTransformer.HANDLER_HANDSHAKE, PipelineTransformer.HANDLER_REWIND_DECODER,
						nettyDecoder);
				pipeline.addBefore(PipelineTransformer.HANDLER_HANDSHAKE, PipelineTransformer.HANDLER_REWIND_ENCODER,
						nettyEncoder);
				pipeline.fireUserEventTriggered(EnumPipelineEvent.EAGLER_INJECTED_REWIND_HANDLERS);
			}

			@Override
			public void injectNettyHandlers0(ChannelHandler nettyCodec) {
				ChannelPipeline pipeline = ctx.pipeline();
				pipeline.addBefore(PipelineTransformer.HANDLER_HANDSHAKE, PipelineTransformer.HANDLER_REWIND_CODEC,
						nettyCodec);
				pipeline.fireUserEventTriggered(EnumPipelineEvent.EAGLER_INJECTED_REWIND_HANDLERS);
			}

		};
		((IEaglerXRewindProtocol<Object, Object>) protocol).initializeConnection(protocolVers, initializer);
		if (initializer.isCanceled()) {
			kickLegacy(ctx, protocolVers);
			return;
		}
		if (!initializer.isInjected()) {
			kickLegacy(ctx);
			server.logger().error(
					"Cancelling EaglerXRewind connection for protocol " + protocol + ", no handlers were injected");
			return;
		}
		if (!initializer.isHandshake()) {
			kickLegacy(ctx);
			server.logger().error(
					"Cancelling EaglerXRewind connection for protocol " + protocol + ", no handshake was injected");
			return;
		}
		pipelineData.rewindProtocol = protocol;
		pipelineData.rewindProtocolVersion = protocolVers;
		RewindMessageInjector injector = initializer.getMessageInjector();
		if (injector != null) {
			ChannelPipeline pipeline = ctx.pipeline();
			if (pipeline.get(PipelineTransformer.HANDLER_REWIND_CODEC) != null) {
				pipeline.addBefore(PipelineTransformer.HANDLER_REWIND_CODEC,
						PipelineTransformer.HANDLER_REWIND_INJECTOR, RewindInjectedMessageHandler.INSTANCE);
			} else {
				pipeline.addBefore(PipelineTransformer.HANDLER_REWIND_ENCODER,
						PipelineTransformer.HANDLER_REWIND_INJECTOR, RewindInjectedMessageHandler.INSTANCE);
			}
		}
		pipelineData.rewindMessageControllerHandle = initializer.getMessageControllerHandle();
		int eaglerProtocol = initializer.getEaglerProtocol();
		switch (eaglerProtocol) {
		case 1:
			if (protocols.isProtocolLegacyAllowed()) {
				HandshakerV1 hs = new HandshakerV1(server, pipelineData, this);
				handshaker = hs;
				hs.init(ctx, initializer.getMinecraftProtocol(), initializer.getEaglerClientBrand(),
						initializer.getEaglerClientVersion());
			}
			break;
		case 2:
			if (protocols.isProtocolLegacyAllowed()) {
				HandshakerV2 hs = new HandshakerV2(server, pipelineData, this);
				handshaker = hs;
				hs.init(ctx, initializer.getEaglerClientBrand(), initializer.getEaglerClientVersion(),
						initializer.getMinecraftProtocol(), initializer.isAuthEnabled(), initializer.getAuthUsername());
			}
			break;
		case 3:
			if (protocols.isProtocolV3Allowed()) {
				HandshakerV3 hs = new HandshakerV3(server, pipelineData, this);
				handshaker = hs;
				hs.init(ctx, initializer.getEaglerClientBrand(), initializer.getEaglerClientVersion(),
						initializer.getMinecraftProtocol(), initializer.isAuthEnabled(), initializer.getAuthUsername());
			}
			break;
		case 4:
			if (protocols.isProtocolV4Allowed()) {
				HandshakerV4 hs = new HandshakerV4(server, pipelineData, this);
				handshaker = hs;
				hs.init(ctx, initializer.getEaglerClientBrand(), initializer.getEaglerClientVersion(),
						initializer.getMinecraftProtocol(), initializer.isAuthEnabled(), initializer.getAuthUsername());
			}
			break;
		case 5:
			if (protocols.isProtocolV5Allowed()) {
				HandshakerV5 hs = new HandshakerV5(server, pipelineData, this);
				handshaker = hs;
				hs.init(ctx, initializer.getEaglerClientBrand(), initializer.getEaglerClientVersion(),
						initializer.getMinecraftProtocol(), initializer.isAuthEnabled(), initializer.getAuthUsername());
			}
			break;
		default:
			break;
		}
		if (handshaker == null) {
			kickLegacy(ctx);
			server.logger().error("Cancelling EaglerXRewind connection for protocol " + protocol
					+ ", unsupported injected handshake version " + eaglerProtocol);
			return;
		}
	}

	private void kickLegacy(ChannelHandlerContext ctx) {
		kickLegacy(ctx, -1);
	}

	public static byte[] prepareRedirectAddr(String addr) {
		int len = addr.length();
		byte[] ret = new byte[2 + len * 2];
		ByteBuf buf = Unpooled.wrappedBuffer(ret).writerIndex(0);
		buf.writeShort(len);
		for (int i = 0; i < len; ++i) {
			buf.writeChar(addr.charAt(i));
		}
		return ret;
	}

	private void kickLegacy(ChannelHandlerContext ctx, int redirectCandidate) {
		terminated = true;
		if (redirectCandidate == 69 && pipelineData.listenerInfo != null
				&& pipelineData.listenerInfo.getLegacyRedirectAddressBuf() != null) {
			byte[] d = pipelineData.listenerInfo.getLegacyRedirectAddressBuf();
			ByteBuf buf = ctx.alloc().buffer(LEGACY_REDIRECT.length + d.length);
			buf.writeBytes(LEGACY_REDIRECT);
			buf.writeBytes(d);
			ctx.writeAndFlush(buf).addListener((ChannelFutureListener) (f) -> {
				f.channel().eventLoop().schedule(() -> {
					if (f.channel().isActive()) {
						f.channel().close();
					}
				}, 100l, TimeUnit.MILLISECONDS);
			});
		} else {
			ctx.writeAndFlush(Unpooled.wrappedBuffer(LEGACY_KICK)).addListener((ChannelFutureListener) (f) -> {
				f.channel().eventLoop().schedule(() -> {
					if (f.channel().isActive()) {
						f.channel().close();
					}
				}, 250l, TimeUnit.MILLISECONDS);
			});
		}
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> output) throws Exception {
		if (!terminated && ctx.channel().isActive()) {
			if (vanillaInitializer != null) {
				vanillaInitializer.handleInbound(ctx, msg);
			} else {
				throw new IllegalStateException("Received an unexpected packet before the connection was initialized");
			}
		}
		output.add(Unpooled.EMPTY_BUFFER); // :(
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		pipelineData.cancelLoginTimeoutHelper();
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		if (vanillaInitializer != null) {
			vanillaInitializer.release();
		}
	}

	public void beginBackendHandshake(ChannelHandlerContext ctx) {
		if (ctx.channel().isActive()) {
			ChannelPipeline pipeline = ctx.pipeline();
			pipeline.remove(PipelineTransformer.HANDLER_OUTBOUND_THROW);
			pipeline.fireUserEventTriggered(EnumPipelineEvent.EAGLER_OUTBOUND_THROW_REMOVED);
			vanillaInitializer = new VanillaInitializer(server, pipelineData, this);
			vanillaInitializer.init(ctx);
		}
	}

	public void handleBackendHandshakeSuccess(ChannelHandlerContext ctx, String acceptedUsername, UUID acceptedUUID) {
		handshaker.handleBackendHandshakeSuccess(ctx, acceptedUsername, acceptedUUID);
	}

	public void enterPlayState(ChannelHandlerContext ctx) {
		pipelineData.cancelLoginTimeoutHelper();
		handshaker.finish(ctx);
		ChannelPipeline pipeline = ctx.pipeline();
		pipeline.fireUserEventTriggered(EnumPipelineEvent.EAGLER_HANDSHAKE_COMPLETE);
		vanillaInitializer.flushBufferedPackets(ctx);
		pipeline.remove(PipelineTransformer.HANDLER_HANDSHAKE);
		pipelineData.signalPlayState();
		pipeline.fireUserEventTriggered(EnumPipelineEvent.EAGLER_ENTERED_PLAY_STATE);
	}

}
