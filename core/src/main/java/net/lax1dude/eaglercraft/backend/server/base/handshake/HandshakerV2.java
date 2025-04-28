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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftAuthCheckRequiredEvent;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.BufferUtils;
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

	public void init(ChannelHandlerContext ctx, String eaglerBrand, String eaglerVersionString, int minecraftVersion,
			boolean auth, byte[] authUsername) {
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
		return inboundHandler.sendErrorCode(ctx, getVersion(),
				HandshakePacketTypes.SERVER_ERROR_AUTHENTICATION_REQUIRED,
				HandshakePacketTypes.AUTHENTICATION_REQUIRED + " [" + getAuthTypeId(authMethod) + "] " + message);
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

			return ctx.writeAndFlush(buffer.retain());
		} finally {
			buffer.release();
		}
	}

	@Override
	protected ChannelFuture sendPacketVersionAuth(ChannelHandlerContext ctx, int selectedEaglerProtocol,
			int selectedMinecraftProtocol, String serverBrand, String serverVersion,
			IEaglercraftAuthCheckRequiredEvent.EnumAuthType authMethod, byte[] authSaltingData,
			boolean nicknameSelection) {
		int authMethId = getAuthTypeId(authMethod);

		if (authMethId == -1) {
			inboundHandler.terminateInternalError(ctx, getVersion());
			pipelineData.connectionLogger.error("Unsupported authentication method resolved: " + authMethod);
			return null;
		}

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

			buffer.writeByte(authMethId);
			if (authSaltingData != null) {
				buffer.writeShort(authSaltingData.length);
				buffer.writeBytes(authSaltingData);
			} else {
				buffer.writeShort(0);
			}

			return ctx.writeAndFlush(buffer.retain());
		} finally {
			buffer.release();
		}
	}

	protected int getAuthTypeId(IEaglercraftAuthCheckRequiredEvent.EnumAuthType meth) {
		return switch (meth) {
		case PLAINTEXT -> 255; // plaintext authentication
		case EAGLER_SHA256 -> 1; // eagler_sha256 authentication
		case AUTHME_SHA256 -> 2; // authme_sha256 authentication
		default -> -1;
		};
	}

}
