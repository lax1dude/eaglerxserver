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
		int pktId = in.readUnsignedByte();
		//TODO: switch statement translate inbound 1.5 to 1.8
		ByteBuf bb = null;
		switch (pktId) {
			case 0x00:
				bb = ctx.alloc().buffer();
				BufferUtils.writeVarInt(bb, 0x00);
				BufferUtils.writeVarInt(bb, in.readInt());
				break;
			case 0x03:
				bb = ctx.alloc().buffer();
				BufferUtils.writeVarInt(bb, 0x01);
				BufferUtils.writeMCString(bb, BufferUtils.readLegacyMCString(in, 100), 100);
				break;

		}
		if (bb != null) {
			out.add(bb);
		}
		in.skipBytes(in.readableBytes());
	}

}
