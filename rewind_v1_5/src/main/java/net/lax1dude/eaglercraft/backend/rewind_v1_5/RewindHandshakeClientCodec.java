package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class RewindHandshakeClientCodec<PlayerObject> extends RewindChannelHandler.Codec<PlayerObject> {

	protected String username;

	protected RewindHandshakeClientCodec<PlayerObject> begin(RewindHandshakeServerCodec<PlayerObject> clientHandshake) {
		
		return this;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		throw new IllegalStateException("Received an unexpected packet before the handshake was completed");
	}

	protected void enterPlayState(ChannelHandlerContext ctx) {
		handler().setEncoder(new RewindPacketEncoder<>());
		handler().setDecoder(new RewindPacketDecoder<>());
	}

}
