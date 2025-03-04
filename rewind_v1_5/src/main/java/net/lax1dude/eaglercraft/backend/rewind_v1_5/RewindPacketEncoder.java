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
		//TODO: switch statement translate outbound 1.8 to 1.5
		// plan: when first handshake packet received, take its data, and turn it into 1.8 handshake. then switch to play mode (once 1.8 confirms?)
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
