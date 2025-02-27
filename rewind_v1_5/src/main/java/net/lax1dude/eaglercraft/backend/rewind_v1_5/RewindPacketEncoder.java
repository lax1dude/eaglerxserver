package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class RewindPacketEncoder<PlayerObject> extends MessageToMessageEncoder<ByteBuf> {

	private final PlayerInstance<PlayerObject> player;

	public RewindPacketEncoder(PlayerInstance<PlayerObject> player) {
		this.player = player;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int pktId = in.readUnsignedByte();
		//TODO: switch statement translate 1.5 to 1.8
	}

}
