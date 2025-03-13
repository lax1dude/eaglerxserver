package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class RewindHandshakeServerCodec<PlayerObject> extends RewindChannelHandler.Codec<PlayerObject> {

	protected RewindHandshakeServerCodec<PlayerObject> begin(RewindHandshakeClientCodec<PlayerObject> clientHandshake) {
		
		return this;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		
	}

	protected void enterPlayState() {
		handler().setEncoder(new RewindPacketEncoder<>());
		handler().setDecoder(new RewindPacketDecoder<>());
	}

}
