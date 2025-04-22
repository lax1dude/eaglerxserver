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
import net.lax1dude.eaglercraft.backend.server.api.EnumCapabilityType;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.NettyPipelineData;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.WebSocketEaglerInitialHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class HandshakerV3 extends HandshakerV2 {

	public HandshakerV3(EaglerXServer<?> server, NettyPipelineData pipelineData,
			WebSocketEaglerInitialHandler inboundHandler) {
		super(server, pipelineData, inboundHandler);
	}

	public void init(ChannelHandlerContext ctx, String eaglerBrand, String eaglerVersionString,
			int minecraftVersion, boolean auth, byte[] authUsername) {
		handlePacketInit(ctx, eaglerBrand, eaglerVersionString, minecraftVersion, auth, authUsername);
	}

	@Override
	protected int getVersion() {
		return 3;
	}

	@Override
	protected GamePluginMessageProtocol getFinalVersion() {
		return GamePluginMessageProtocol.V3;
	}

	private static final int CAPABILITIES_MASK = EnumCapabilityType.UPDATE.getBit() | EnumCapabilityType.VOICE.getBit();
	private static final int[] CAPABILITIES_VER = new int[] { 1, 1 };

	@Override
	protected int fallbackCapabilityMask() {
		return CAPABILITIES_MASK;
	}

	@Override
	protected int[] fallbackCapabilityVers() {
		return CAPABILITIES_VER;
	}

	@Override
	protected ChannelFuture sendPacketDenyLogin(ChannelHandlerContext ctx, Object component) {
		return sendPacketDenyLogin(ctx, server.componentHelper().serializeLegacyJSON(component));
	}

	@Override
	protected ChannelFuture sendPacketDenyLogin(ChannelHandlerContext ctx, String message) {
		if(message.length() > 65535) {
			message = message.substring(0, 65535);
		}
		ByteBuf buffer = ctx.alloc().buffer();
		try {
			buffer.writeByte(HandshakePacketTypes.PROTOCOL_SERVER_DENY_LOGIN);
			byte[] msg = message.getBytes(StandardCharsets.UTF_8);
			int len = msg.length;
			if(len > 65535) {
				len = 65535;
			}
			buffer.writeShort(len);
			buffer.writeBytes(msg, 0, len);
			return ctx.writeAndFlush(buffer.retain());
		}finally {
			buffer.release();
		}
	}

}
