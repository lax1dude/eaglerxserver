package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class RewindPacketDecoder<PlayerObject> extends MessageToMessageDecoder<ByteBuf> {

	private final PlayerInstance<PlayerObject> player;

	public RewindPacketDecoder(PlayerInstance<PlayerObject> player) {
		this.player = player;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int pktId = BufferUtils.readVarInt(in, 5);
		//TODO: switch statement translate 1.8 to 1.5
	}

}
