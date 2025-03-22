package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class RewindPacketDecoder<PlayerObject> extends RewindChannelHandler.Decoder<PlayerObject> {

	// TODO: look into 0x0C Steer Vehicle
	// TODO: rewrite to use individual named methods for each packet
	// TODO: on sneak, if riding, dismount

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
					BufferUtils.convertLegacyMCString(in, bb, 100);
					break;
				case 0x07:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x02);
					in.readInt();
					BufferUtils.writeVarInt(bb, in.readInt());
					BufferUtils.writeVarInt(bb, in.readBoolean() ? 1 : 0);
					break;
				case 0x0A:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x03);
					bb.writeBoolean(in.readBoolean());
					break;
				case 0x0B:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x04);
					double ppx = in.readDouble();
					double ppy = in.readDouble();
					in.readDouble();
					double ppz = in.readDouble();
					player().setPos(ppx, ppy, ppz);
					bb.writeDouble(ppx);
					bb.writeDouble(ppy);
					bb.writeDouble(ppz);
					bb.writeBoolean(in.readBoolean());
					break;
				case 0x0C:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x05);
					float plyaw = in.readFloat();
					float plpitch = in.readFloat();
					player().setLook(plyaw, plpitch);
					bb.writeFloat(plyaw);
					bb.writeFloat(plpitch);
					bb.writeBoolean(in.readBoolean());
					break;
				case 0x0D:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x06);
					double pplx = in.readDouble();
					double pply = in.readDouble();
					in.readDouble();
					double pplz = in.readDouble();
					float pplyaw = in.readFloat();
					float pplpitch = in.readFloat();
					player().setPos(pplx, pply, pplz);
					player().setLook(pplyaw, pplpitch);
					bb.writeDouble(pplx);
					bb.writeDouble(pply);
					bb.writeDouble(pplz);
					bb.writeFloat(pplyaw);
					bb.writeFloat(pplpitch);
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
					bb.writeByte(in.readUnsignedByte());
					BufferUtils.convertLegacySlot(in, bb, nbtContext(), tempBuffer());
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
					BufferUtils.writeVarInt(bb, in.readUnsignedByte() - 1);
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
					BufferUtils.convertLegacySlot(in, bb, nbtContext(), tempBuffer());
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
					BufferUtils.convertLegacySlot(in, bb, nbtContext(), tempBuffer());
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
					BufferUtils.convertLegacyMCString(in, bb, 255);
					bb.writeBoolean(false);
					break;
				case 0xCC:
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x15);
					BufferUtils.convertLegacyMCString(in, bb, 255);
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
					String name = BufferUtils.readLegacyMCString(in, 255);
					int pmLen = in.readShort();
					if (name.equals("MC|AdvCdm")) {
						int ri = in.readerIndex();
						int cmdX = in.readInt();
						int cmdY = in.readInt();
						int cmdZ = in.readInt();
						String cmd = BufferUtils.readLegacyMCString(in, 32767);
						in.readerIndex(ri);
						in.writerIndex(ri);
						in.writeByte(0);
						in.writeInt(cmdX);
						in.writeInt(cmdY);
						in.writeInt(cmdZ);
						BufferUtils.writeMCString(in, cmd, 32767);
						in.writeBoolean(true);
						pmLen = in.writerIndex() - ri;
					} else if (name.equals("MC|BEdit") || name.equals("MC|BSign")) {
						int ri = in.readerIndex();
						bb = ctx.alloc().buffer();
						BufferUtils.convertLegacySlot(in, bb, nbtContext(), tempBuffer());
						in.readerIndex(ri);
						in.writerIndex(ri);
						in.writeBytes(bb);
						pmLen = bb.writerIndex();
						bb.release();
					}
					bb = ctx.alloc().buffer();
					BufferUtils.writeVarInt(bb, 0x17);
					BufferUtils.writeMCString(bb, name, 255);
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
			e.printStackTrace();
			if (bb != null) {
				bb.release();
			}
		}
		in.skipBytes(in.readableBytes());
		if (out.isEmpty()) {
			out.add(Unpooled.EMPTY_BUFFER);
		}
	}

}
