package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.math.BigInteger;
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
		//TODO: switch statement translate inbound 1.5 to 1.8
		ByteBuf bb = null;
		switch (pktId) {
			case 0x00:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x00);
				bb.writeInt(BufferUtils.readVarInt(in));
				break;
			case 0x01:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x01);
				int entityId = in.readInt();
				short gamemode = in.readUnsignedByte();
				byte dimension = in.readByte();
				short difficulty = in.readUnsignedByte();
				short maxPlayers = in.readUnsignedByte();
				String levelType = BufferUtils.readMCString(in, 255);
				bb.writeInt(entityId);
				BufferUtils.writeLegacyMCString(bb, levelType, 255);
				bb.writeByte(gamemode);
				bb.writeByte(dimension);
				bb.writeByte(difficulty);
				bb.writeByte(0);
				bb.writeByte(maxPlayers);
				break;
			case 0x02:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x03);
				// todo: json to legacy
				BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 32767), 32767);
				break;
			case 0x03:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x04);
				bb.writeLong(in.readLong());
				bb.writeLong(in.readLong());
				break;
			case 0x04:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x05);
				int eid = BufferUtils.readVarInt(in);
				short slot = in.readShort();
				ByteBuf slotData = BufferUtils.convertSlot2Legacy(in, ctx.alloc());
				bb.writeInt(eid);
				bb.writeShort(slot);
				bb.writeBytes(slotData);
				break;
			case 0x05:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x06);
				BigInteger bi = new BigInteger(Long.toUnsignedString(in.readLong()));
				int x = bi.shiftRight(38).intValue();
				int y = bi.shiftLeft(26).intValue() & 0xFFF;
				int z = bi.shiftLeft(38).shiftRight(38).intValue();
				bb.writeInt(x);
				bb.writeInt(y);
				bb.writeInt(z);
				break;
			case 0x06:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x08);
				float health = in.readFloat();
				int food = BufferUtils.readVarInt(in);
				float foodSaturation = in.readFloat();
				bb.writeShort((int) Math.ceil(health));
				bb.writeShort(food);
				bb.writeFloat(foodSaturation);
				break;
			case 0x07:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x09);
				int dim = in.readInt();
				short diff = in.readUnsignedByte();
				short gm = in.readUnsignedByte();
				String lt = BufferUtils.readMCString(in, 255);
				bb.writeInt(dim);
				bb.writeByte(diff);
				bb.writeByte(gm);
				bb.writeShort(256);
				BufferUtils.writeLegacyMCString(bb, lt, 255);
				break;
			case 0x08:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x0D);
				double plx = in.readDouble();
				double ply = in.readDouble();
				double plz = in.readDouble();
				float plyaw = in.readFloat();
				float plpitch = in.readFloat();
				byte flags = in.readByte(); // todo: aaa
				// todo: track player pos at all times!!
				// <Dinnerbone> It's a bitfield, X/Y/Z/Y_ROT/X_ROT. If X is set, the x value is relative and not absolute.
				// X 0x01 Y 0x02 Z 0x04 Y_ROT 0x08 X_ROT 0x10
				bb.writeDouble(plx);
				bb.writeDouble(ply);
				bb.writeDouble(ply);
				bb.writeDouble(plz);
				bb.writeFloat(plyaw);
				bb.writeFloat(plpitch);
				bb.writeBoolean(true); // on ground status, todo: track on ground status instead of only showing true
				break;
			case 0x09:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x10);
				bb.writeShort(in.readByte());
				break;
			case 0x0A:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x11);
				int beid = BufferUtils.readVarInt(in);
				BigInteger bbi = new BigInteger(Long.toUnsignedString(in.readLong()));
				int bx = bbi.shiftRight(38).intValue();
				int by = bbi.shiftLeft(26).intValue() & 0xFFF;
				int bz = bbi.shiftLeft(38).shiftRight(38).intValue();
				bb.writeInt(beid);
				bb.writeByte(0);
				bb.writeInt(bx);
				bb.writeByte(by);
				bb.writeInt(bz);
				break;
			case 0x0B:
				int aeid = BufferUtils.readVarInt(in);
				short animation = in.readUnsignedByte();
				if (animation >= 0 && animation <= 3) {
					bb = ctx.alloc().buffer();
					bb.writeByte(0x12);
					bb.writeInt(aeid);
					bb.writeByte(animation + 1);
				}
				break;
			case 0x0C:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x14);
				int peid = BufferUtils.readVarInt(in);
				// uuid:
				long pmsb = in.readLong();
				long plsb = in.readLong();
				int px = in.readInt();
				int py = in.readInt();
				int pz = in.readInt();
				byte pyaw = in.readByte();
				byte ppitch = in.readByte();
				short pitem = in.readShort();
				// todo: metadata :(

				bb.writeInt(peid);
				// todo: use metadata to get username!!
				BufferUtils.writeLegacyMCString(bb, pmsb + " " + plsb, 255);
				bb.writeInt(px);
				bb.writeInt(py);
				bb.writeInt(pz);
				bb.writeByte(pyaw);
				bb.writeByte(ppitch);
				bb.writeShort(pitem);
				// todo: metadata :(
				bb.writeByte((short) 0xFF); // temp, no metadata...
				break;
			case 0x0D:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x16);
				bb.writeInt(BufferUtils.readVarInt(in));
				bb.writeInt(BufferUtils.readVarInt(in));
				break;
			case 0x0E:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x17);
				int oeid = BufferUtils.readVarInt(in);
				byte otype = in.readByte();
				int ox = in.readInt();
				int oy = in.readInt();
				int oz = in.readInt();
				byte opitch = in.readByte();
				byte oyaw = in.readByte();
				int odata = in.readInt();
				bb.writeInt(oeid);
				bb.writeByte(otype);
				bb.writeInt(ox);
				bb.writeInt(oy);
				bb.writeInt(oz);
				bb.writeByte(opitch);
				bb.writeByte(oyaw);
				// todo: verify object data between versions!!
				bb.writeByte(odata);
				if (odata != 0) {
					short ovx = in.readShort();
					short ovy = in.readShort();
					short ovz = in.readShort();
					out.add(bb);
					bb = ctx.alloc().buffer();
					bb.writeByte(0x1C);
					bb.writeInt(oeid);
					bb.writeShort(ovx);
					bb.writeShort(ovy);
					bb.writeShort(ovz);
				}
				break;
			case 0x0F:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x18);
				int meid = BufferUtils.readVarInt(in);
				short mtype = in.readUnsignedByte();
				int mx = in.readInt();
				int my = in.readInt();
				int mz = in.readInt();
				byte myaw = in.readByte();
				byte mpitch = in.readByte();
				byte mhpitch = in.readByte();
				short mvx = in.readShort();
				short mvy = in.readShort();
				short mvz = in.readShort();
				// todo: entity metadata!!!!!!!

				bb.writeInt(meid);
				bb.writeByte(mtype);
				bb.writeInt(mx);
				bb.writeInt(my);
				bb.writeInt(mz);
				bb.writeByte(mpitch);
				bb.writeByte(mhpitch);
				bb.writeByte(myaw);
				bb.writeByte(mvx);
				bb.writeByte(mvy);
				bb.writeByte(mvz);
				// todo: entity metadata!!!!!!!
				bb.writeByte((short) 0xFF); // temp, no metadata...
				break;
			case 0x10:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x19);
				int paintEid = BufferUtils.readVarInt(in);
				String paintTitle = BufferUtils.readMCString(in, 13);
				BigInteger paintbi = new BigInteger(Long.toUnsignedString(in.readLong()));
				int paintx = paintbi.shiftRight(38).intValue();
				int painty = paintbi.shiftLeft(26).intValue() & 0xFFF;
				int paintz = paintbi.shiftLeft(38).shiftRight(38).intValue();
				short paintDir = in.readUnsignedByte();
				bb.writeInt(paintEid);
				BufferUtils.writeLegacyMCString(bb, paintTitle, 13);
				bb.writeInt(paintx);
				bb.writeInt(painty);
				bb.writeInt(paintz);
				bb.writeInt(paintDir);
				break;
			case 0x11:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x1A);
				bb.writeInt(BufferUtils.readVarInt(in));
				bb.writeInt(in.readInt());
				bb.writeInt(in.readInt());
				bb.writeInt(in.readInt());
				bb.writeShort(in.readShort());
				break;
			case 0x12:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x1C);
				bb.writeInt(BufferUtils.readVarInt(in));
				bb.writeShort(in.readShort());
				bb.writeShort(in.readShort());
				bb.writeShort(in.readShort());
				break;
			case 0x13:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x1D);
				int c = BufferUtils.readVarInt(in);
				bb.writeByte(c);
				for (int i = 0; i < c; ++i) {
					bb.writeInt(BufferUtils.readVarInt(in));
				}
				break;
			case 0x14:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x1E);
				bb.writeInt(BufferUtils.readVarInt(in));
				break;
			case 0x15:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x1F);
				bb.writeInt(BufferUtils.readVarInt(in));
				bb.writeByte(in.readByte());
				bb.writeByte(in.readByte());
				bb.writeByte(in.readByte());
				break;
			case 0x16:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x20);
				bb.writeInt(BufferUtils.readVarInt(in));
				bb.writeByte(in.readByte());
				bb.writeByte(in.readByte());
				break;
			case 0x17:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x21);
				bb.writeInt(BufferUtils.readVarInt(in));
				bb.writeByte(in.readByte());
				bb.writeByte(in.readByte());
				bb.writeByte(in.readByte());
				bb.writeByte(in.readByte());
				bb.writeByte(in.readByte());
				break;
			case 0x18:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x22);
				bb.writeInt(BufferUtils.readVarInt(in));
				bb.writeInt(in.readInt());
				bb.writeInt(in.readInt());
				bb.writeInt(in.readInt());
				bb.writeInt(in.readInt());
				bb.writeByte(in.readByte());
				bb.writeByte(in.readByte());
				break;
			case 0x19:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x23);
				bb.writeInt(BufferUtils.readVarInt(in));
				bb.writeByte(in.readByte());
				break;
			case 0x1A:
				int i = in.readInt();
				byte b = in.readByte();
				if (b <= 17) {
					bb = ctx.alloc().buffer();
					bb.writeByte(0x26);
					bb.writeInt(i);
					bb.writeByte(b);
				}
				break;
			case 0x1B:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x27);
				bb.writeInt(in.readInt());
				bb.writeInt(in.readInt());
				break;
			case 0x1C:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x28);
				bb.writeInt(BufferUtils.readVarInt(in));
				// todo: metadata :(
				bb.writeByte((short) 0xFF); // temp, no metadata...
				break;
			case 0x1D:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x29);
				bb.writeInt(BufferUtils.readVarInt(in));
				bb.writeByte(in.readByte());
				bb.writeByte(in.readByte());
				bb.writeShort(BufferUtils.readVarInt(in));
				break;
			case 0x1E:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x2A);
				bb.writeInt(BufferUtils.readVarInt(in));
				bb.writeByte(in.readByte());
				break;
			case 0x1F:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x2B);
				bb.writeFloat(in.readFloat());
				bb.writeShort(BufferUtils.readVarInt(in));
				bb.writeShort(BufferUtils.readVarInt(in));
				break;
			case 0x20:
				break;
			case 0x21:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x33);
				int chunkX = in.readInt();
				int chunkZ = in.readInt();
				boolean chunkCont = in.readBoolean();
				int chunkPbm = in.readUnsignedShort();
				BufferUtils.readVarInt(in);
				ByteBuf chunkData = BufferUtils.convertChunk2Legacy(true, chunkPbm, chunkCont, in, ctx.alloc());
				bb.writeInt(chunkX);
				bb.writeInt(chunkZ);
				bb.writeBoolean(chunkCont);
				bb.writeShort(chunkPbm);
				bb.writeShort(0);
				// todo: compress with zlib deflate
				bb.writeInt(chunkData.readableBytes());
				bb.writeBytes(chunkData);
				break;
			case 0x22:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x34);
				int mbcX = in.readInt();
				int mbcZ = in.readInt();
				int mbcRecCount = BufferUtils.readVarInt(in);
				bb.writeInt(mbcX);
				bb.writeInt(mbcZ);
				bb.writeShort(mbcRecCount);
				bb.writeInt(mbcRecCount * 4);
				for (int ii = 0; ii < mbcRecCount; ++ii) {
					short recHorizPos = in.readUnsignedByte();
					short recYCoord = in.readUnsignedByte();
					int recBlockId = BufferUtils.readVarInt(in);
					byte recXCoord = (byte) ((recHorizPos >> 4) & 0x0F);
					byte recZCoord = (byte) (recHorizPos & 0x0F);
					// todo: conv to legacy as needed
					int recBlockType = recBlockId >> 4;
					int recBlockMeta = recBlockId & 15;
					bb.writeInt((recBlockMeta & 0xF) | ((recBlockType & 0xFFF) << 4) | ((recYCoord & 0xFF) << 16) | ((recZCoord & 0xF) << 24) | ((recXCoord & 0xF) << 28));
				}
				break;
			case 0x23:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x35);
				BigInteger bcbi = new BigInteger(Long.toUnsignedString(in.readLong()));
				int bcx = bcbi.shiftRight(38).intValue();
				int bcy = bcbi.shiftLeft(26).intValue() & 0xFFF;
				int bcz = bcbi.shiftLeft(38).shiftRight(38).intValue();
				int bcbid = BufferUtils.readVarInt(in);
				int bcbt = bcbid >> 4;
				int bcbm = bcbid & 15;
				bb.writeInt(bcx);
				bb.writeByte(bcy);
				bb.writeInt(bcz);
				bb.writeShort(bcbt);
				bb.writeByte(bcbm);
				break;
			case 0x24:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x36);
				BigInteger babi = new BigInteger(Long.toUnsignedString(in.readLong()));
				int bax = babi.shiftRight(38).intValue();
				int bay = babi.shiftLeft(26).intValue() & 0xFFF;
				int baz = babi.shiftLeft(38).shiftRight(38).intValue();
				short baa = in.readShort();
				int bat = BufferUtils.readVarInt(in);
				bb.writeInt(bax);
				bb.writeShort(bay);
				bb.writeShort(baz);
				bb.writeShort(baa);
				bb.writeShort(bat);
				break;
			case 0x25:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x37);
				int bbaEid = BufferUtils.readVarInt(in);
				BigInteger bbabi = new BigInteger(Long.toUnsignedString(in.readLong()));
				int bbax = bbabi.shiftRight(38).intValue();
				int bbay = bbabi.shiftLeft(26).intValue() & 0xFFF;
				int bbaz = bbabi.shiftLeft(38).shiftRight(38).intValue();
				byte bbas = in.readByte();
				bb.writeInt(bbaEid);
				bb.writeInt(bbax);
				bb.writeInt(bbay);
				bb.writeInt(bbaz);
				bb.writeByte(bbas);
				break;
			case 0x26:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x38);
				boolean mcbSkyLightSent = in.readBoolean();
				int mcbCcc = BufferUtils.readVarInt(in);
				int[][] mcbChunkMeta = new int[mcbCcc][];
				for (int ii = 0; ii < mcbCcc; ++ii) {
					mcbChunkMeta[ii] = new int[] {
							in.readInt(),
							in.readInt(),
							in.readUnsignedShort()
					};
				}
				ByteBuf guhBuf = ctx.alloc().buffer();
				for (int ii = 0; ii < mcbCcc; ++ii) {
					guhBuf.writeBytes(BufferUtils.convertChunk2Legacy(mcbSkyLightSent, mcbChunkMeta[ii][2], true, in, ctx.alloc()));
				}
				bb.writeShort(mcbCcc);
				bb.writeInt(guhBuf.readableBytes());
				bb.writeBoolean(mcbSkyLightSent);
				// todo: compress!!
				bb.writeBytes(guhBuf);
				for (int ii = 0; ii < mcbCcc; ++ii) {
					bb.writeInt(mcbChunkMeta[ii][0]);
					bb.writeInt(mcbChunkMeta[ii][1]);
					bb.writeShort(mcbChunkMeta[ii][2]);
					bb.writeShort(0);
				}
				break;
			case 0x27:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x3C);
				bb.writeDouble(in.readFloat());
				bb.writeDouble(in.readFloat());
				bb.writeDouble(in.readFloat());
				bb.writeFloat(in.readFloat());
				int explRecCnt = in.readInt();
				bb.writeInt(explRecCnt);
				bb.writeBytes(in, explRecCnt * 3);
				bb.writeFloat(in.readFloat());
				bb.writeFloat(in.readFloat());
				bb.writeFloat(in.readFloat());
				break;
			case 0x28:

				break;
		}
		if (bb != null) {
			out.add(bb);
		}
		in.skipBytes(in.readableBytes());
	}

}
