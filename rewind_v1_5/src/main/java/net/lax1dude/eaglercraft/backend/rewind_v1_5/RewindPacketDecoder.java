package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class RewindPacketDecoder<PlayerObject> extends RewindChannelHandler.Decoder<PlayerObject> {

	// TODO: look into 0x0C Steer Vehicle
	// TODO: rewrite to use individual named methods for each packet

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int pktId = in.readUnsignedByte();
		ByteBuf bb = null;
		try {
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
				case 0x07:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x02);
					in.readInt();
					BufferUtils.writeVarInt(bb, in.readInt());
					bb.writeByte(in.readBoolean() ? 1 : 0);
					break;
				case 0x0A:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x03);
					bb.writeBoolean(in.readBoolean());
					break;
				case 0x0B:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x04);
					bb.writeDouble(in.readDouble());
					bb.writeDouble(in.readDouble());
					in.readDouble();
					bb.writeDouble(in.readDouble());
					bb.writeBoolean(in.readBoolean());
					break;
				case 0x0C:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x05);
					bb.writeFloat(in.readFloat());
					bb.writeFloat(in.readFloat());
					bb.writeBoolean(in.readBoolean());
					break;
				case 0x0D:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x06);
					bb.writeDouble(in.readDouble());
					bb.writeDouble(in.readDouble());
					in.readDouble();
					bb.writeDouble(in.readDouble());
					bb.writeFloat(in.readFloat());
					bb.writeFloat(in.readFloat());
					bb.writeBoolean(in.readBoolean());
					break;
				case 0x0E:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x07);
					bb.writeByte(in.readByte());
					bb.writeLong(BufferUtils.createPosition(in.readInt(), in.readUnsignedByte(), in.readInt()));
					bb.writeByte(in.readByte());
					break;
				case 0x0F:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x08);
					bb.writeLong(BufferUtils.createPosition(in.readInt(), in.readUnsignedByte(), in.readInt()));
					bb.writeByte(in.readByte());
					BufferUtils.convertLegacySlot(in, bb);
					bb.writeByte(in.readByte());
					bb.writeByte(in.readByte());
					bb.writeByte(in.readByte());
					break;
				case 0x10:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x09);
					bb.writeShort(in.readShort());
					break;
				case 0x12:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x0A);
					break;
				case 0x13:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x0B);
					BufferUtils.writeVarInt(bb, in.readInt());
					BufferUtils.writeVarInt(bb, in.readByte());
					BufferUtils.writeVarInt(bb, 0);
					break;
				case 0x65:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x0D);
					bb.writeByte(in.readByte());
					break;
				case 0x66:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x0E);
					bb.writeByte(in.readByte());
					bb.writeShort(in.readShort());
					bb.writeByte(in.readByte());
					bb.writeShort(in.readShort());
					bb.writeByte(in.readByte());
					BufferUtils.convertLegacySlot(in, bb);
					break;
				case 0x6A:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x0F);
					bb.writeByte(in.readByte());
					bb.writeShort(in.readShort());
					bb.writeBoolean(in.readBoolean());
					break;
				case 0x6B:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x10);
					bb.writeShort(in.readShort());
					BufferUtils.convertLegacySlot(in, bb);
					break;
				case 0x6C:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x11);
					bb.writeByte(in.readByte());
					bb.writeByte(in.readByte());
					break;
				case 0x82:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x12);
					bb.writeLong(BufferUtils.createPosition(in.readInt(), in.readShort(), in.readInt()));
					for (int ii = 0; ii < 4; ++ii) {
						BufferUtils.writeMCString(bb, BufferUtils.stringToChat(BufferUtils.readLegacyMCString(in, 255)), 4095);
					}
					break;
				case 0xCA:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x13);
					bb.writeByte(in.readByte());
					bb.writeFloat(in.readByte());
					bb.writeFloat(in.readByte());
					break;
				case 0xCB:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x14);
					BufferUtils.writeMCString(bb, BufferUtils.readLegacyMCString(in, 255), 255);
					bb.writeBoolean(false);
					break;
				case 0xCC:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x15);
					BufferUtils.writeMCString(bb, BufferUtils.readLegacyMCString(in, 255), 255);
					bb.writeByte(16 >> in.readByte());
					byte guh = in.readByte();
					bb.writeByte(guh & 3);
					bb.writeBoolean((guh & 8) != 0);
					in.readByte();
					bb.writeByte(in.readBoolean() ? 0xFF : 0xFE);
					break;
				case 0xCD:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x16);
					BufferUtils.writeVarInt(bb, 0);
					break;
				case 0xFA:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x17);
					BufferUtils.writeMCString(bb, BufferUtils.readLegacyMCString(in, 255), 255);
					short pmLen = in.readShort();
					bb.writeBytes(in, pmLen);
					break;
				case 0xFE:
					bb = ctx.alloc().buffer();
					bb.writeBytes(in, 2);
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
