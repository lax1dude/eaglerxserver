package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.List;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.md_5.bungee.chat.ComponentSerializer;

public class RewindPacketEncoder<PlayerObject> extends MessageToMessageEncoder<ByteBuf> {

	private final PlayerInstance<PlayerObject> player;

	public RewindPacketEncoder(PlayerInstance<PlayerObject> player) {
		this.player = player;
	}

	private static final String[] particleNames = new String[] {
			"explode",
			"largeexplosion",
			"hugeexplosion",
			"fireworksSpark",
			"bubble",
			"splash",
			"", // wake
			"suspended",
			"depthsuspend",
			"crit",
			"magicCrit",
			"smoke",
			"largesmoke",
			"spell",
			"instantSpell",
			"mobSpell",
			"mobSpellAmbient",
			"witchMagic",
			"dripWater",
			"dripLava",
			"angryVillager",
			"happyVillager",
			"townaura",
			"note",
			"portal",
			"enchantmenttable",
			"flame",
			"lava",
			"footstep",
			"cloud",
			"reddust",
			"snowballpoof",
			"snowshovel",
			"slime",
			"heart",
			"", // barrier
			"iconcrack_*", // iconcrack_(id)
			"blockcrack_*_*", // blockcrack_(id)_(data)
			"blockdust_*", // blockdust_(id)
			"", // droplet
			"", // take
			"" // mobappearance
	};

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int pktId = BufferUtils.readVarInt(in);
		//TODO: switch statement translate outbound 1.8 to 1.5
		// plan: when first handshake packet received, take its data, and turn it into 1.8 handshake. then switch to play mode (once 1.8 confirms?)
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
				BufferUtils.writeLegacyMCString(bb, ComponentSerializer.deserialize(BufferUtils.readMCString(in, 32767)).toLegacyText(), 32767);
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
				bb.writeInt(eid);
				bb.writeShort(slot);
				BufferUtils.convertSlot2Legacy(in, bb);
				break;
			case 0x05:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x06);
				long xyz = in.readLong();
				bb.writeInt(BufferUtils.posX(xyz));
				bb.writeInt(BufferUtils.posY(xyz));
				bb.writeInt(BufferUtils.posZ(xyz));
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
				long bxyz = in.readLong();
				bb.writeInt(beid);
				bb.writeByte(0);
				bb.writeInt(BufferUtils.posX(bxyz));
				bb.writeByte(BufferUtils.posY(bxyz));
				bb.writeInt(BufferUtils.posZ(bxyz));
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
				long paintxyz = in.readLong();
				short paintDir = in.readUnsignedByte();
				bb.writeInt(paintEid);
				BufferUtils.writeLegacyMCString(bb, paintTitle, 13);
				bb.writeInt(BufferUtils.posX(paintxyz));
				bb.writeInt(BufferUtils.posY(paintxyz));
				bb.writeInt(BufferUtils.posZ(paintxyz));
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
				ByteBuf chunkData = ctx.alloc().buffer();
				BufferUtils.convertChunk2Legacy(true, chunkPbm, chunkCont, in, chunkData);
				bb.writeInt(chunkX);
				bb.writeInt(chunkZ);
				bb.writeBoolean(chunkCont);
				bb.writeShort(chunkPbm);
				bb.writeShort(0);
				ByteBuf chunkData2 = ctx.alloc().buffer();
				player.getPlayer().getServerAPI().createNativeZlib(true, false, 6).getNettyUnsafe().deflate(chunkData, chunkData2);
				chunkData.release();
				bb.writeInt(chunkData2.readableBytes());
				bb.writeBytes(chunkData2);
				chunkData2.release();
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
				long bcxyz = in.readLong();
				int bcbid = BufferUtils.readVarInt(in);
				int bcbt = bcbid >> 4;
				int bcbm = bcbid & 15;
				bb.writeInt(BufferUtils.posX(bcxyz));
				bb.writeByte(BufferUtils.posY(bcxyz));
				bb.writeInt(BufferUtils.posZ(bcxyz));
				bb.writeShort(bcbt);
				bb.writeByte(bcbm);
				break;
			case 0x24:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x36);
				long baxyz = in.readLong();
				short baa = in.readShort();
				int bat = BufferUtils.readVarInt(in);
				bb.writeInt(BufferUtils.posX(baxyz));
				bb.writeShort(BufferUtils.posY(baxyz));
				bb.writeInt(BufferUtils.posZ(baxyz));
				bb.writeShort(baa);
				bb.writeShort(bat);
				break;
			case 0x25:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x37);
				int bbaEid = BufferUtils.readVarInt(in);
				long bbaxyz = in.readLong();
				byte bbas = in.readByte();
				bb.writeInt(bbaEid);
				bb.writeInt(BufferUtils.posX(bbaxyz));
				bb.writeInt(BufferUtils.posY(bbaxyz));
				bb.writeInt(BufferUtils.posZ(bbaxyz));
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
					BufferUtils.convertChunk2Legacy(mcbSkyLightSent, mcbChunkMeta[ii][2], true, in, guhBuf);
				}
				bb.writeShort(mcbCcc);
				ByteBuf guhBuf2 = ctx.alloc().buffer();
				player.getPlayer().getServerAPI().createNativeZlib(true, false, 6).getNettyUnsafe().deflate(guhBuf, guhBuf2);
				guhBuf.release();
				bb.writeInt(guhBuf2.readableBytes());
				bb.writeBoolean(mcbSkyLightSent);
				bb.writeBytes(guhBuf2);
				guhBuf2.release();
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
				bb = ctx.alloc().buffer();
				bb.writeByte(0x3D);
				bb.writeInt(in.readInt());
				long effPos = in.readLong();
				bb.writeInt(BufferUtils.posX(effPos));
				bb.writeByte(BufferUtils.posY(effPos));
				bb.writeInt(BufferUtils.posZ(effPos));
				bb.writeInt(in.readInt());
				bb.writeBoolean(in.readBoolean());
				break;
			case 0x29:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x3E);
				BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 255);
				bb.writeInt(in.readInt());
				bb.writeInt(in.readInt());
				bb.writeInt(in.readInt());
				bb.writeFloat(in.readFloat());
				bb.writeByte(in.readUnsignedByte());
				break;
			case 0x2A:
				int pId = in.readInt();
				String pName = particleNames[pId];
				if (!pName.isEmpty()) {
					bb = ctx.alloc().buffer();
					bb.writeByte(0x3F);
					in.readBoolean();
					float[] pFloats = new float[]{
							in.readFloat(),
							in.readFloat(),
							in.readFloat(),
							in.readFloat(),
							in.readFloat(),
							in.readFloat(),
							in.readFloat()
					};
					int pInt = in.readInt();
					while (pName.contains("*")) {
						pName = pName.replaceFirst("\\*", "" + BufferUtils.readVarInt(in));
					}
					BufferUtils.writeLegacyMCString(bb, pName, 255);
					for (float pFloat : pFloats) {
						bb.writeFloat(pFloat);
					}
					bb.writeInt(pInt);
				}
				break;
			case 0x2B:
				short cgsReason = in.readUnsignedByte();
				if (cgsReason >= 0 && cgsReason <= 4) {
					bb = ctx.alloc().buffer();
					bb.writeByte(0x46);
					bb.writeByte(cgsReason);
					float cgsValue = in.readFloat();
					if (cgsValue > 1) cgsValue = 0;
					bb.writeByte((int) cgsValue);
				}
				break;
			case 0x2C:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x47);
				bb.writeInt(BufferUtils.readVarInt(in));
				bb.writeByte(in.readByte());
				bb.writeInt(in.readInt());
				bb.writeInt(in.readInt());
				bb.writeInt(in.readInt());
				break;
			case 0x2D:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x64);
				bb.writeByte(in.readUnsignedByte());
				String windowType = BufferUtils.readMCString(in, 255);
				// todo: window type string to int
				bb.writeByte(0); // temp to keep aligned
				String windowTitle = BufferUtils.readMCString(in, 4095);
				windowTitle = ComponentSerializer.deserialize(windowTitle).toLegacyText();
				BufferUtils.writeLegacyMCString(bb, windowTitle, 255);
				bb.writeByte(in.readUnsignedByte());
				bb.writeBoolean(!windowTitle.isEmpty());
				break;
			case 0x2E:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x65);
				bb.writeByte(in.readUnsignedByte());
				break;
			case 0x2F:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x67);
				bb.writeByte(in.readByte());
				bb.writeShort(in.readShort());
				BufferUtils.convertSlot2Legacy(in, bb);
				break;
			case 0x30:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x68);
				bb.writeByte(in.readUnsignedByte());
				short numSlots = in.readShort();
				bb.writeShort(numSlots);
				for (int ii = 0; ii < numSlots; ++ii) {
					BufferUtils.convertSlot2Legacy(in, bb);
				}
				break;
			case 0x31:
				short grah = in.readUnsignedByte();
				short uwpProp = in.readShort();
				// todo: ideally detect furnace vs ench table by tracking window id creation etc etc
				// for now, 2 --> 0 and 0 --> 1
				if (uwpProp == 2) {
					uwpProp = 0;
				} else if (uwpProp == 0) {
					uwpProp = 1;
				}
				if (uwpProp <= 2) {
					bb = ctx.alloc().buffer();
					bb.writeByte(0x69);
					bb.writeByte(grah);
					bb.writeShort(uwpProp);
					bb.writeShort(in.readShort());
				}
				break;
			case 0x32:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x6A);
				bb.writeByte(in.readByte());
				bb.writeShort(in.readShort());
				bb.writeBoolean(in.readBoolean());
				break;
			case 0x33:
				bb = ctx.alloc().buffer();
				bb.writeByte(0x82);
				long signPos = in.readLong();
				bb.writeInt(BufferUtils.posX(signPos));
				bb.writeShort(BufferUtils.posY(signPos));
				bb.writeInt(BufferUtils.posZ(signPos));
				for (int ii = 0; ii < 4; ++ii) {
					BufferUtils.writeLegacyMCString(bb, ComponentSerializer.deserialize(BufferUtils.readMCString(in, 4095)).toLegacyText(), 255);
				}
				break;
			case 0x34:
				// todo: all this bullshit
				/*
				bb = ctx.alloc().buffer();
				bb.writeByte(0x83);
				int mapId = BufferUtils.readVarInt(in);
				byte mapScale
				*/

				break;
			case 0x35:
				long ubePos = in.readLong();
				short ubeAct = in.readUnsignedByte();
				if (ubeAct == 1) {
					bb = ctx.alloc().buffer();
					bb.writeByte(0x84);
					bb.writeInt(BufferUtils.posX(ubePos));
					bb.writeShort(BufferUtils.posY(ubePos));
					bb.writeInt(BufferUtils.posZ(ubePos));
					bb.writeByte(ubeAct);
					BufferUtils.convertNBT2Legacy(in, bb);
				}
				break;
			case 0x36:
				// not exist, todo: perhaps log in chat or polyfill with chat?
				break;
			case 0x37:
				int numStats = BufferUtils.readVarInt(in);
				for (int ii = 0; ii < numStats; ++ii) {
					String statName = BufferUtils.readMCString(in, 255);
					int statId = -1;
					if (statName.equals("stat.leaveGame")) {
						statId = 1004;
					} else if (statName.equals("stat.playOneMinute")) {
						statId = 1100;
					} else if (statName.equals("stat.walkOneCm")) {
						statId = 2000;
					} else if (statName.equals("stat.swimOneCm")) {
						statId = 2001;
					} else if (statName.equals("stat.fallOneCm")) {
						statId = 2002;
					} else if (statName.equals("stat.climbOneCm")) {
						statId = 2003;
					} else if (statName.equals("stat.flyOneCm")) {
						statId = 2004;
					} else if (statName.equals("stat.diveOneCm")) {
						statId = 2005;
					} else if (statName.equals("stat.minecartOneCm")) {
						statId = 2006;
					} else if (statName.equals("stat.boatOneCm")) {
						statId = 2007;
					} else if (statName.equals("stat.pigOneCm")) {
						statId = 2008;
					} else if (statName.equals("stat.jump")) {
						statId = 2010;
					} else if (statName.equals("stat.drop")) {
						statId = 2011;
					} else if (statName.equals("stat.damageDealt")) {
						statId = 2020;
					} else if (statName.equals("stat.damageTaken")) {
						statId = 2021;
					} else if (statName.equals("stat.deaths")) {
						statId = 2022;
					} else if (statName.equals("stat.mobKills")) {
						statId = 2023;
					} else if (statName.equals("stat.playerKills")) {
						statId = 2024;
					} else if (statName.equals("stat.fishCaught")) {
						statId = 2025;
					} else if (statName.equals("achievement.openInventory")) {
						statId = 5242880;
					} else if (statName.equals("achievement.mineWood")) {
						statId = 5242881;
					} else if (statName.equals("achievement.buildWorkBench")) {
						statId = 5242882;
					} else if (statName.equals("achievement.buildPickaxe")) {
						statId = 5242883;
					} else if (statName.equals("achievement.buildFurnace")) {
						statId = 5242884;
					} else if (statName.equals("achievement.acquireIron")) {
						statId = 5242885;
					} else if (statName.equals("achievement.buildHoe")) {
						statId = 5242886;
					} else if (statName.equals("achievement.makeBread")) {
						statId = 5242887;
					} else if (statName.equals("achievement.bakeCake")) {
						statId = 5242888;
					} else if (statName.equals("achievement.buildBetterPickaxe")) {
						statId = 5242889;
					} else if (statName.equals("achievement.cookFish")) {
						statId = 5242890;
					} else if (statName.equals("achievement.onARail")) {
						statId = 5242891;
					} else if (statName.equals("achievement.buildSword")) {
						statId = 5242892;
					} else if (statName.equals("achievement.killEnemy")) {
						statId = 5242893;
					} else if (statName.equals("achievement.killCow")) {
						statId = 5242894;
					} else if (statName.equals("achievement.flyPig")) {
						statId = 5242895;
					} else {
						statName = null;
					}
					if (statName != null) {
						bb = ctx.alloc().buffer();
						bb.writeByte(0xC8);
						bb.writeInt(statId);
						bb.writeByte(1); // guess that it only goes up by 1 lol
						out.add(bb);
					}
				}
				bb = null;
				break;
			case 0x38:
				// TODO: CONTINUE FROM HERE: "PLAYER LIST ITEM" PACKET
				int pliAction = BufferUtils.readVarInt(in);
				if (pliAction != 1) {
					int pliNum = BufferUtils.readVarInt(in);
					for (int ii = 0; ii < pliNum; ++ii) {
						bb = ctx.alloc().buffer();
						bb.writeByte(0xC9);
						long plimsb = in.readLong();
						long plilsb = in.readLong();
						String pliName = player.getPlayer().getServerAPI().getPlayerByUUID(new UUID(plimsb, plilsb)).getUsername();
						if (pliAction != 0) {
							// remove player
							// todo: need to keep internal map of username to current display name!!
						}
						if (pliAction == 2) {
							// set ping
						} else if (pliAction == 3) {
							if (in.readBoolean()) {
								pliName = ComponentSerializer.deserialize(BufferUtils.readMCString(in, 255)).toLegacyText();
							}
						}
						if (pliAction != 4) {
							// add player
						}
					}
					bb = null;
				}
				break;
		}
		if (bb != null) {
			out.add(bb);
		}
	}

}
