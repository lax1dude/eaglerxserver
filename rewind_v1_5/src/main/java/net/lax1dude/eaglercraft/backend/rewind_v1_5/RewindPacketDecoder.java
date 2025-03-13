package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class RewindPacketDecoder<PlayerObject> extends RewindChannelHandler.Decoder<PlayerObject> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int pktId = in.readUnsignedByte();
		//TODO: switch statement translate inbound 1.5 to 1.8
		ByteBuf bb = ctx.alloc().buffer();
		try {
			switch (pktId) {
				case 0x00:
					BufferUtils.writeVarInt(bb, 0x00);
					BufferUtils.writeVarInt(bb, in.readInt());
					break;
				case 0x03:
					BufferUtils.writeVarInt(bb, 0x01);
					BufferUtils.writeMCString(bb, BufferUtils.readLegacyMCString(in, 100), 100);
					break;
				case 0x07:
					break;
				case 0x0A:
					break;
				case 0x0B:
					break;
				case 0x0C:
					break;
				case 0x0D:
					break;
				case 0x0E:
					break;
				case 0x0F:
					break;
				case 0x10:
					break;
				case 0x12:
					break;
				case 0x13:
					break;
				case 0x65:
					break;
				case 0x66:
					break;
				case 0x6A:
					break;
				case 0x6B:
					break;
				case 0x6C:
					break;
				case 0x82:
					break;
				case 0xCA:
					break;
				case 0xCB:
					break;
				case 0xCC:
					break;
				case 0xCD:
					break;
				case 0xFA:
					break;
				case 0xFE:
					break;
				case 0xFF:
					break;
			}
			if (bb != null) {
				out.add(bb);
			}
		} catch (Exception e) {
			if (bb != null) {
				bb.release();
			}
		}
	}

}
