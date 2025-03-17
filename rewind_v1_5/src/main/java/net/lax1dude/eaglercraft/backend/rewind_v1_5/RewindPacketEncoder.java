package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.*;
import java.util.zip.DataFormatException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;

public class RewindPacketEncoder<PlayerObject> extends RewindChannelHandler.Encoder<PlayerObject> {

	private static final class TabListItem {
		public String name;
		public int ping;

		public TabListItem(String name, int ping) {
			this.name = name;
			this.ping = ping;
		}
	}

	private final Map<UUID, TabListItem> tabList = new HashMap<>();

	private final Map<String, Map<String, Integer>> scoreBoard = new HashMap<>();

	private double playerX = 0;
	private double playerY = 0;
	private double playerZ = 0;
	private float playerYaw = 0;
	private float playerPitch = 0;

	private final Set<Short> enchWindows = new HashSet<>();
	private final Set<Short> furnWindows = new HashSet<>();

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

	private void handleKeepAlive(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x00);
		bb.writeInt(BufferUtils.readVarInt(in));
	}

	private void handleJoinGame(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x01);
		bb.writeInt(in.readInt());
		short gamemode = in.readUnsignedByte();
		byte dimension = in.readByte();
		short difficulty = in.readUnsignedByte();
		short maxPlayers = in.readUnsignedByte();
		String levelType = BufferUtils.readMCString(in, 255);
		BufferUtils.writeLegacyMCString(bb, levelType, 255);
		bb.writeByte(gamemode);
		bb.writeByte(dimension);
		bb.writeByte(difficulty);
		bb.writeByte(0);
		bb.writeByte(maxPlayers);
	}

	private void handleChatMessage(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x03);
		BufferUtils.writeLegacyMCString(bb, serverAPI().getComponentHelper().convertJSONToLegacySection(BufferUtils.readMCString(in, 32767)), 32767);
	}

	private void handleTimeUpdate(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x04);
		bb.writeLong(in.readLong());
		bb.writeLong(in.readLong());
	}

	private void handleEntityEquipment(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x05);
		int eid = BufferUtils.readVarInt(in);
		short slot = in.readShort();
		bb.writeInt(eid);
		bb.writeShort(slot);
		BufferUtils.convertSlot2Legacy(in, bb);
	}

	private void handleSpawnPosition(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x06);
		long xyz = in.readLong();
		bb.writeInt(BufferUtils.posX(xyz));
		bb.writeInt(BufferUtils.posY(xyz));
		bb.writeInt(BufferUtils.posZ(xyz));
	}

	private void handleUpdateHealth(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x08);
		float health = in.readFloat();
		int food = BufferUtils.readVarInt(in);
		float foodSaturation = in.readFloat();
		bb.writeShort((int) Math.ceil(health));
		bb.writeShort(food);
		bb.writeFloat(foodSaturation);
	}

	private void handleRespawn(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x09);
		bb.writeInt(in.readInt());
		bb.writeByte(in.readUnsignedByte());
		bb.writeByte(in.readUnsignedByte());
		bb.writeShort(256);
		BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 255);
	}

	private void handlePlayerPositionAndLook(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x0D);
		double plx = in.readDouble();
		double ply = in.readDouble();
		double plz = in.readDouble();
		float plyaw = in.readFloat();
		float plpitch = in.readFloat();
		byte flags = in.readByte();
		playerX = plx + ((flags & 0x01) != 0 ? playerX : 0);
		playerY = ply + ((flags & 0x02) != 0 ? playerY : 0);
		playerZ = plz + ((flags & 0x04) != 0 ? playerZ : 0);
		playerYaw = plyaw + ((flags & 0x10) != 0 ? playerYaw : 0);
		playerPitch = plpitch + ((flags & 0x08) != 0 ? playerPitch : 0);
		bb.writeDouble(playerX);
		bb.writeDouble(playerY);
		bb.writeDouble(playerY);
		bb.writeDouble(playerZ);
		bb.writeFloat(plyaw);
		bb.writeFloat(plpitch);
		bb.writeBoolean(false);
	}

	private void handleHeldItemChange(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x10);
		bb.writeShort(in.readByte());
	}

	private void handleUseBed(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x11);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeByte(0);
		long bxyz = in.readLong();
		bb.writeInt(BufferUtils.posX(bxyz));
		bb.writeByte(BufferUtils.posY(bxyz));
		bb.writeInt(BufferUtils.posZ(bxyz));
	}

	private boolean handleAnimation(ByteBuf in, ByteBuf bb) {
		int aeid = BufferUtils.readVarInt(in);
		short animation = in.readUnsignedByte();
		if (animation >= 0 && animation <= 3) {
			bb.writeByte(0x12);
			bb.writeInt(aeid);
			bb.writeByte(animation + 1);
			return true;
		}
		return false;
	}

	private void handleSpawnPlayer(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x14);
		bb.writeInt(BufferUtils.readVarInt(in));
		BufferUtils.writeLegacyMCString(bb, serverAPI().getPlayerByUUID(new UUID(in.readLong(), in.readLong())).getUsername(), 255);
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeByte(in.readByte());
		bb.writeByte(in.readByte());
		bb.writeShort(in.readShort());
		BufferUtils.convertMetadata2Legacy(in, bb);
	}

	private void handleCollectItem(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x16);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeInt(BufferUtils.readVarInt(in));
	}

	private void handleSpawnObject(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x17);
		bb.writeInt(BufferUtils.readVarInt(in));
		int otype = in.readByte();
		bb.writeByte(otype);
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeByte(in.readByte());
		bb.writeByte(in.readByte());
		int odata = in.readInt();
		bb.writeByte(odata);
		if (odata != 0) {
			bb.writeShort(in.readShort());
			bb.writeShort(in.readShort());
			bb.writeShort(in.readShort());
		}
	}

	private void handleSpawnMob(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x18);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeByte(in.readUnsignedByte());
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		byte myaw = in.readByte();
		byte mpitch = in.readByte();
		byte mhpitch = in.readByte();
		bb.writeByte(mpitch);
		bb.writeByte(mhpitch);
		bb.writeByte(myaw);
		bb.writeShort(in.readShort());
		bb.writeShort(in.readShort());
		bb.writeShort(in.readShort());
		BufferUtils.convertMetadata2Legacy(in, bb);
	}

	private void handleSpawnPainting(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x19);
		bb.writeInt(BufferUtils.readVarInt(in));
		BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 13), 13);
		long paintxyz = in.readLong();
		bb.writeInt(BufferUtils.posX(paintxyz));
		bb.writeInt(BufferUtils.posY(paintxyz));
		bb.writeInt(BufferUtils.posZ(paintxyz));
		bb.writeInt(in.readUnsignedByte());
	}

	private void handleSpawnExperienceOrb(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x1A);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeShort(in.readShort());
	}

	private void handleEntityVelocity(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x1C);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeShort(in.readShort());
		bb.writeShort(in.readShort());
		bb.writeShort(in.readShort());
	}

	private void handleDestroyEntities(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x1D);
		int c = BufferUtils.readVarInt(in);
		bb.writeByte(c);
		for (int i = 0; i < c; ++i) {
			bb.writeInt(BufferUtils.readVarInt(in));
		}
	}

	private void handleEntity(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x1E);
		bb.writeInt(BufferUtils.readVarInt(in));
	}

	private void handleEntityRelativeMove(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x1F);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeByte(in.readByte());
		bb.writeByte(in.readByte());
		bb.writeByte(in.readByte());
	}

	private void handleEntityLook(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x20);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeByte(in.readByte());
		bb.writeByte(in.readByte());
	}

	private void handleEntityLookAndRelativeMove(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x21);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeByte(in.readByte());
		bb.writeByte(in.readByte());
		bb.writeByte(in.readByte());
		bb.writeByte(in.readByte());
		bb.writeByte(in.readByte());
	}

	private void handleEntityTeleport(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x22);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeByte(in.readByte());
		bb.writeByte(in.readByte());
	}

	private void handleEntityHeadLook(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x23);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeByte(in.readByte());
	}

	private boolean handleEntityStatus(ByteBuf in, ByteBuf bb) {
		// todo: make sure invalid entity statuses dont make 1.5 explode, e.g. guardian stuff+
		int i = in.readInt();
		byte b = in.readByte();
		if (b <= 17) {
			bb.writeByte(0x26);
			bb.writeInt(i);
			bb.writeByte(b);
			return true;
		}
		return false;
	}

	private void handleAttachEntity(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x27);
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
	}

	private void handleEntityMetadata(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x28);
		bb.writeInt(BufferUtils.readVarInt(in));
		BufferUtils.convertMetadata2Legacy(in, bb);
	}

	private void handleEntityEffect(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x29);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeByte(in.readByte());
		bb.writeByte(in.readByte());
		bb.writeShort(BufferUtils.readVarInt(in));
	}

	private void handleRemoveEntityEffect(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x2A);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeByte(in.readByte());
	}

	private void handleSetExperience(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x2B);
		bb.writeFloat(in.readFloat());
		bb.writeShort(BufferUtils.readVarInt(in));
		bb.writeShort(BufferUtils.readVarInt(in));
	}

	private void handleChunkData(ByteBuf in, ByteBuf bb, ByteBufAllocator alloc) throws DataFormatException {
		bb.writeByte(0x33);
		int chunkX = in.readInt();
		int chunkZ = in.readInt();
		boolean chunkCont = in.readBoolean();
		int chunkPbm = in.readUnsignedShort();
		BufferUtils.readVarInt(in);
		ByteBuf chunkData = alloc.buffer();
		try {
			BufferUtils.convertChunk2Legacy(true, chunkPbm, chunkCont, in, chunkData);
			bb.writeInt(chunkX);
			bb.writeInt(chunkZ);
			bb.writeBoolean(chunkCont);
			bb.writeShort(chunkPbm);
			bb.writeShort(0);
			ByteBuf chunkData2 = alloc.buffer();
			try {
				player().getNativeZlib().netty().deflate(chunkData, chunkData2);
				bb.writeInt(chunkData2.readableBytes());
				bb.writeBytes(chunkData2);
			} finally {
				chunkData2.release();
			}
		} finally {
			chunkData.release();
		}
	}

	private void handleMultiBlockChange(ByteBuf in, ByteBuf bb) {
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
			recBlockId = BufferUtils.convertTypeMeta2Legacy(recBlockId);
			int recBlockType = recBlockId >> 4;
			int recBlockMeta = recBlockId & 15;
			bb.writeInt((recBlockMeta & 0xF) | ((recBlockType & 0xFFF) << 4) | ((recYCoord & 0xFF) << 16) | ((recZCoord & 0xF) << 24) | ((recXCoord & 0xF) << 28));
		}
	}

	private void handleBlockChange(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x35);
		long bcxyz = in.readLong();
		int bcbid = BufferUtils.readVarInt(in);
		bcbid = BufferUtils.convertTypeMeta2Legacy(bcbid);
		int bcbt = bcbid >> 4;
		int bcbm = bcbid & 15;
		bb.writeInt(BufferUtils.posX(bcxyz));
		bb.writeByte(BufferUtils.posY(bcxyz));
		bb.writeInt(BufferUtils.posZ(bcxyz));
		bb.writeShort(bcbt);
		bb.writeByte(bcbm);
	}

	private void handleBlockAction(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x36);
		long baxyz = in.readLong();
		bb.writeInt(BufferUtils.posX(baxyz));
		bb.writeShort(BufferUtils.posY(baxyz));
		bb.writeInt(BufferUtils.posZ(baxyz));
		bb.writeShort(in.readShort());
		bb.writeShort(BufferUtils.convertType2Legacy(BufferUtils.readVarInt(in)));
	}

	private void handleBlockBreakAnimation(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x37);
		bb.writeInt(BufferUtils.readVarInt(in));
		long bbaxyz = in.readLong();
		bb.writeInt(BufferUtils.posX(bbaxyz));
		bb.writeInt(BufferUtils.posY(bbaxyz));
		bb.writeInt(BufferUtils.posZ(bbaxyz));
		bb.writeByte(in.readByte());
	}

	private void handleMapChunkBulk(ByteBuf in, ByteBuf bb, ByteBufAllocator alloc) throws DataFormatException {
		bb.writeByte(0x38);
		boolean mcbSkyLightSent = in.readBoolean();
		int mcbCcc = BufferUtils.readVarInt(in);
		int[][] mcbChunkMeta = new int[mcbCcc][];
		for (int ii = 0; ii < mcbCcc; ++ii) {
			mcbChunkMeta[ii] = new int[]{
					in.readInt(),
					in.readInt(),
					in.readUnsignedShort()
			};
		}
		ByteBuf guhBuf = alloc.buffer();
		try {
			for (int ii = 0; ii < mcbCcc; ++ii) {
				BufferUtils.convertChunk2Legacy(mcbSkyLightSent, mcbChunkMeta[ii][2], true, in, guhBuf);
			}
			bb.writeShort(mcbCcc);
			ByteBuf guhBuf2 = alloc.buffer();
			try {
				player().getNativeZlib().netty().deflate(guhBuf, guhBuf2);
				bb.writeInt(guhBuf2.readableBytes());
				bb.writeBoolean(mcbSkyLightSent);
				bb.writeBytes(guhBuf2);
			} finally {
				guhBuf2.release();
			}
		} finally {
			guhBuf.release();
		}
		for (int ii = 0; ii < mcbCcc; ++ii) {
			bb.writeInt(mcbChunkMeta[ii][0]);
			bb.writeInt(mcbChunkMeta[ii][1]);
			bb.writeShort(mcbChunkMeta[ii][2]);
			bb.writeShort(0);
		}
	}

	private void handleExplosion(ByteBuf in, ByteBuf bb) {
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
	}

	private void handleEffect(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x3D);
		bb.writeInt(in.readInt());
		long effPos = in.readLong();
		bb.writeInt(BufferUtils.posX(effPos));
		bb.writeByte(BufferUtils.posY(effPos));
		bb.writeInt(BufferUtils.posZ(effPos));
		bb.writeInt(in.readInt());
		bb.writeBoolean(in.readBoolean());
	}

	private void handleSoundEffect(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x3E);
		BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 255);
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeFloat(in.readFloat());
		bb.writeByte(in.readUnsignedByte());
	}

	private boolean handleParticle(ByteBuf in, ByteBuf bb) {
		int pId = in.readInt();
		String pName = particleNames[pId];
		if (!pName.isEmpty()) {
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
			return true;
		}
		return false;
	}

	private boolean handleChangeGameState(ByteBuf in, ByteBuf bb) {
		short cgsReason = in.readUnsignedByte();
		if (cgsReason >= 0 && cgsReason <= 4) {
			bb.writeByte(0x46);
			bb.writeByte(cgsReason);
			float cgsValue = in.readFloat();
			if (cgsValue > 1) cgsValue = 0;
			bb.writeByte((int) cgsValue);
			return true;
		}
		return false;
	}

	private void handleSpawnGlobalEntity(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x47);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeByte(in.readByte());
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
	}

	private void handleOpenWindow(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x64);
		short windowUniqueId = in.readUnsignedByte();
		bb.writeByte(windowUniqueId);
		String windowType = BufferUtils.readMCString(in, 255);
		byte windowId = -1;
		switch (windowType) {
			case "minecraft:chest":
			case "EntityHorse": // yeah...
				windowId = 0;
				break;
			case "minecraft:crafting_table":
				windowId = 1;
				break;
			case "minecraft:furnace":
				furnWindows.add(windowUniqueId);
				windowId = 2;
				break;
			case "minecraft:dispenser":
			case "minecraft:dropper":
				windowId = 3;
				break;
			case "minecraft:enchanting_table":
				enchWindows.add(windowUniqueId);
				windowId = 4;
				break;
			case "minecraft:brewing_stand":
				windowId = 5;
				break;
			case "minecraft:villager":
				windowId = 6;
				break;
			case "minecraft:beacon":
				windowId = 7;
				break;
			case "minecraft:anvil":
				windowId = 8;
				break;
			case "minecraft:hopper":
				windowId = 9;
				break;
		}
		bb.writeByte(windowId);
		String windowTitle = BufferUtils.readMCString(in, 4095);
		windowTitle = serverAPI().getComponentHelper().convertJSONToLegacySection(windowTitle);
		BufferUtils.writeLegacyMCString(bb, windowTitle, 255);
		bb.writeByte(in.readUnsignedByte());
		bb.writeBoolean(!windowTitle.isEmpty());
	}

	private void handleCloseWindow(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x65);
		short windowUniqueId = in.readUnsignedByte();
		enchWindows.remove(windowUniqueId);
		furnWindows.remove(windowUniqueId);
		bb.writeByte(windowUniqueId);
	}

	private boolean handleSetSlot(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x67);
		byte windowUniqueId = in.readByte();
		bb.writeByte(windowUniqueId);
		short slot = in.readShort();
		if (enchWindows.contains((short) windowUniqueId) && slot > 0) {
			if (slot == 1) {
				return false;
			}
			bb.writeShort(slot - 1);
		} else {
			bb.writeShort(slot);
		}
		BufferUtils.convertSlot2Legacy(in, bb);
		return true;
	}

	private void handleWindowItems(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x68);
		short windowUniqueId = in.readUnsignedByte();
		bb.writeByte(windowUniqueId);
		short numSlots = in.readShort();
		boolean ench = enchWindows.contains(windowUniqueId);
		if (ench && numSlots > 1) {
			bb.writeShort(numSlots - 1);
		} else {
			bb.writeShort(numSlots);
		}
		for (int ii = 0; ii < numSlots; ++ii) {
			if (ench && ii == 1) {
				int here = bb.writerIndex();
				BufferUtils.convertSlot2Legacy(in, bb);
				bb.writerIndex(here);
			} else {
				BufferUtils.convertSlot2Legacy(in, bb);
			}
		}
	}

	private boolean handleWindowProperty(ByteBuf in, ByteBuf bb) {
		short grah = in.readUnsignedByte();
		short uwpProp = in.readShort();
		if (uwpProp <= 2) {
			if (furnWindows.contains(grah)) {
				if (uwpProp == 2) {
					uwpProp = 0;
				} else if (uwpProp == 0) {
					uwpProp = 1;
				}
			}
			bb.writeByte(0x69);
			bb.writeByte(grah);
			bb.writeShort(uwpProp);
			bb.writeShort(in.readShort());
			return true;
		}
		return false;
	}

	private void handleConfirmTransaction(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x6A);
		bb.writeByte(in.readByte());
		bb.writeShort(in.readShort());
		bb.writeBoolean(in.readBoolean());
	}

	private void handleUpdateSign(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x82);
		long signPos = in.readLong();
		bb.writeInt(BufferUtils.posX(signPos));
		bb.writeShort(BufferUtils.posY(signPos));
		bb.writeInt(BufferUtils.posZ(signPos));
		for (int ii = 0; ii < 4; ++ii) {
			BufferUtils.writeLegacyMCString(bb, serverAPI().getComponentHelper().convertJSONToLegacySection(BufferUtils.readMCString(in, 4095)), 255);
		}
	}

	private void handleMap(ByteBuf in, ByteBuf bb) {
		// todo: all this bullshit (maps)
		/*
		bb = ctx.alloc().buffer();
		bb.writeByte(0x83);
		int mapId = BufferUtils.readVarInt(in);
		byte mapScale
		*/
	}

	private boolean handleUpdateBlockEntity(ByteBuf in, ByteBuf bb) {
		long ubePos = in.readLong();
		short ubeAct = in.readUnsignedByte();
		if (ubeAct == 1 || ubeAct == 3 || ubeAct == 4 || ubeAct == 5) {
			bb.writeByte(0x84);
			bb.writeInt(BufferUtils.posX(ubePos));
			bb.writeShort(BufferUtils.posY(ubePos));
			bb.writeInt(BufferUtils.posZ(ubePos));
			bb.writeByte(ubeAct);
			BufferUtils.convertNBT2Legacy(in, bb);
			return true;
		}
		return false;
	}

	private void handleStatistics(ByteBuf in, ByteBufAllocator alloc, List<Object> out) {
		ByteBuf bb;
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
				bb = alloc.buffer();
				try {
					bb.writeByte(0xC8);
					bb.writeInt(statId);
					bb.writeByte(1); // guess that it only goes up by 1 lol
					out.add(bb.retain());
				} finally {
					bb.release();
				}
			}
		}
	}

	private void handlePlayerListItem(ByteBuf in, ByteBufAllocator alloc, List<Object> out) {
		ByteBuf bb;
		int pliAction = BufferUtils.readVarInt(in);
		if (pliAction != 1) {
			int pliNum = BufferUtils.readVarInt(in);
			for (int ii = 0; ii < pliNum; ++ii) {
				long plimsb = in.readLong();
				long plilsb = in.readLong();
				UUID pliUuid = new UUID(plimsb, plilsb);
				if (!tabList.containsKey(pliUuid)) {
					if (pliAction == 0) {
						String tempName = BufferUtils.readMCString(in, 255);
						int tempSkip = BufferUtils.readVarInt(in);
						for (int iii = 0; iii < tempSkip; ++iii) {
							BufferUtils.readMCString(in, 255);
							BufferUtils.readMCString(in, 255);
							if (in.readBoolean()) {
								BufferUtils.readMCString(in, 255);
							}
						}
						BufferUtils.readVarInt(in);
						int tbPing = BufferUtils.readVarInt(in);
						if (in.readBoolean()) {
							tempName = serverAPI().getComponentHelper().convertJSONToLegacySection(BufferUtils.readMCString(in, 255));
						}
						tabList.put(pliUuid, new TabListItem(tempName, tbPing));
					} else {
						tabList.put(pliUuid, new TabListItem(serverAPI().getPlayerByUUID(pliUuid).getUsername(), 0));
					}
				}
				TabListItem pliItem = tabList.get(pliUuid);
				if (pliAction != 0) {
					bb = alloc.buffer();
					try {
						bb.writeByte(0xC9);
						BufferUtils.writeLegacyMCString(bb, pliItem.name, 255);
						bb.writeBoolean(false);
						bb.writeShort(pliItem.ping);
						out.add(bb.retain());
					} finally {
						bb.release();
					}
				}
				if (pliAction == 2) {
					bb = alloc.buffer();
					try {
						bb.writeByte(0xC9);
						BufferUtils.writeLegacyMCString(bb, pliItem.name, 255);
						bb.writeBoolean(true);
						bb.writeShort(pliItem.ping = BufferUtils.readVarInt(in));
						out.add(bb.retain());
					} finally {
						bb.release();
					}
				} else if (pliAction == 3) {
					if (in.readBoolean()) {
						pliItem.name = serverAPI().getComponentHelper().convertJSONToLegacySection(BufferUtils.readMCString(in, 255));
					}
				}
				if (pliAction != 4) {
					bb = alloc.buffer();
					try {
						bb.writeByte(0xC9);
						BufferUtils.writeLegacyMCString(bb, pliItem.name, 255);
						bb.writeBoolean(true);
						bb.writeShort(pliItem.ping);
						out.add(bb.retain());
					} finally {
						bb.release();
					}
				} else {
					tabList.remove(pliUuid);
				}
			}
		}
	}

	private void handlePlayerAbilities(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0xCA);
		byte paFlags = in.readByte();
		if ((paFlags & 0x01) != ((paFlags & 0x08) >> 3)) {
			paFlags ^= 0x09;
		}
		bb.writeByte(paFlags);
		bb.writeByte((int) in.readFloat());
		bb.writeByte((int) in.readFloat());
	}

	private void handleTabComplete(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0xCB);
		int tcCount = BufferUtils.readVarInt(in);
		StringBuilder tcSb = new StringBuilder();
		for (int ii = 0; ii < tcCount; ++ii) {
			tcSb.append(BufferUtils.readMCString(in, 255));
			if (ii + 1 < tcCount) {
				tcSb.append("\u0000");
			}
		}
		BufferUtils.writeLegacyMCString(bb, tcSb.toString(), 32767);
	}

	private void handleScoreboardObjective(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0xCE);
		String sboName = BufferUtils.readMCString(in, 255);
		BufferUtils.writeLegacyMCString(bb, sboName, 255);
		byte sboMode = in.readByte();
		if (sboMode == 0) {
			scoreBoard.put(sboName, new HashMap<>());
		} else if (sboMode == 1) {
			scoreBoard.remove(sboName);
		}
		BufferUtils.writeLegacyMCString(bb, sboMode == 1 ? "" : BufferUtils.readMCString(in, 255), 255);
		bb.writeByte(sboMode);
	}

	private void handleUpdateScore(ByteBuf in, ByteBufAllocator alloc, List<Object> out) {
		ByteBuf bb;
		String sbItem = BufferUtils.readMCString(in, 255);
		byte usAction = in.readByte();
		String sbName = BufferUtils.readMCString(in, 255);
		if (scoreBoard.containsKey(sbName)) {
			if (usAction == 1) {
				Map<String, Integer> guhhh = scoreBoard.get(sbName);
				guhhh.remove(sbItem);
				if (guhhh.isEmpty()) {
					scoreBoard.remove(sbName);
				}
				bb = alloc.buffer();
				try {
					bb.writeByte(0xCF);
					BufferUtils.writeLegacyMCString(bb, sbItem, 255);
					bb.writeByte(1);
					out.add(bb.retain());
				} finally {
					bb.release();
				}
				for (String s : scoreBoard.keySet()) {
					Map<String, Integer> argh = scoreBoard.get(s);
					if (argh.containsKey(sbItem)) {
						bb = alloc.buffer();
						try {
							bb.writeByte(0xCF);
							BufferUtils.writeLegacyMCString(bb, sbItem, 255);
							bb.writeByte(0);
							BufferUtils.writeLegacyMCString(bb, s, 255);
							bb.writeInt(argh.get(sbItem));
							out.add(bb.retain());
						} finally {
							bb.release();
						}
					}
				}
			} else {
				int sbVal = BufferUtils.readVarInt(in);
				scoreBoard.get(sbName).put(sbItem, sbVal);
				bb = alloc.buffer();
				try {
					bb.writeByte(0xCF);
					BufferUtils.writeLegacyMCString(bb, sbItem, 255);
					bb.writeByte(usAction);
					BufferUtils.writeLegacyMCString(bb, sbName, 255);
					bb.writeInt(sbVal);
					out.add(bb.retain());
				} finally {
					bb.release();
				}
			}
		}
	}

	private void handleDisplayScoreboard(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0xD0);
		bb.writeByte(in.readByte());
		BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 255);
	}

	private void handleTeams(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0xD1);
		BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 255);
		byte teamMode = in.readByte();
		if (teamMode == 0 || teamMode == 2) {
			BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 255);
			BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 255);
			BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 255);
			bb.writeByte(in.readByte());
			BufferUtils.readMCString(in, 255);
			in.readByte(); // team color, does not exist in 1.5, maybe fake it by appending to display name, prefix, and suffix???
		}
		if (teamMode == 0 || teamMode == 3 || teamMode == 4) {
			int teamPlNum = BufferUtils.readVarInt(in);
			bb.writeShort(teamPlNum);
			for (int ii = 0; ii < teamPlNum; ++ii) {
				BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 40), 40);
			}
		}
	}

	private void handlePluginMessage(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0xFA);
		BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 255);
		int pmLen = in.readableBytes();
		bb.writeShort(pmLen);
		bb.writeBytes(in, pmLen);
	}

	private void handleDisconnect(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0xFF);
		BufferUtils.writeLegacyMCString(bb, serverAPI().getComponentHelper().convertJSONToLegacySection(BufferUtils.readMCString(in, 32767)), 32767);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int pktId = BufferUtils.readVarInt(in);
		ByteBuf bb = ctx.alloc().buffer();
		try {
			switch (pktId) {
				case 0x00:
					handleKeepAlive(in, bb);
					break;
				case 0x01:
					handleJoinGame(in, bb);
					break;
				case 0x02:
					handleChatMessage(in, bb);
					break;
				case 0x03:
					handleTimeUpdate(in, bb);
					break;
				case 0x04:
					handleEntityEquipment(in, bb);
					break;
				case 0x05:
					handleSpawnPosition(in, bb);
					break;
				case 0x06:
					handleUpdateHealth(in, bb);
					break;
				case 0x07:
					handleRespawn(in, bb);
					break;
				case 0x08:
					handlePlayerPositionAndLook(in, bb);
					break;
				case 0x09:
					handleHeldItemChange(in, bb);
					break;
				case 0x0A:
					handleUseBed(in, bb);
					break;
				case 0x0B:
					if (!handleAnimation(in, bb)) {
						bb.release();
						bb = null;
					}
					break;
				case 0x0C:
					handleSpawnPlayer(in, bb);
					break;
				case 0x0D:
					handleCollectItem(in, bb);
					break;
				case 0x0E:
					handleSpawnObject(in, bb);
					break;
				case 0x0F:
					handleSpawnMob(in, bb);
					break;
				case 0x10:
					handleSpawnPainting(in, bb);
					break;
				case 0x11:
					handleSpawnExperienceOrb(in, bb);
					break;
				case 0x12:
					handleEntityVelocity(in, bb);
					break;
				case 0x13:
					handleDestroyEntities(in, bb);
					break;
				case 0x14:
					handleEntity(in, bb);
					break;
				case 0x15:
					handleEntityRelativeMove(in, bb);
					break;
				case 0x16:
					handleEntityLook(in, bb);
					break;
				case 0x17:
					handleEntityLookAndRelativeMove(in, bb);
					break;
				case 0x18:
					handleEntityTeleport(in, bb);
					break;
				case 0x19:
					handleEntityHeadLook(in, bb);
					break;
				case 0x1A:
					if (!handleEntityStatus(in, bb)) {
						bb.release();
						bb = null;
					}
					break;
				case 0x1B:
					handleAttachEntity(in, bb);
					break;
				case 0x1C:
					handleEntityMetadata(in, bb);
					break;
				case 0x1D:
					handleEntityEffect(in, bb);
					break;
				case 0x1E:
					handleRemoveEntityEffect(in, bb);
					break;
				case 0x1F:
					handleSetExperience(in, bb);
					break;
				case 0x20:
					bb.release();
					bb = null;
					break;
				case 0x21:
					handleChunkData(in, bb, ctx.alloc());
					break;
				case 0x22:
					handleMultiBlockChange(in, bb);
					break;
				case 0x23:
					handleBlockChange(in, bb);
					break;
				case 0x24:
					handleBlockAction(in, bb);
					break;
				case 0x25:
					handleBlockBreakAnimation(in, bb);
					break;
				case 0x26:
					handleMapChunkBulk(in, bb, ctx.alloc());
					break;
				case 0x27:
					handleExplosion(in, bb);
					break;
				case 0x28:
					handleEffect(in, bb);
					break;
				case 0x29:
					handleSoundEffect(in, bb);
					break;
				case 0x2A:
					if (!handleParticle(in, bb)) {
						bb.release();
						bb = null;
					}
					break;
				case 0x2B:
					if (!handleChangeGameState(in, bb)) {
						bb.release();
						bb = null;
					}
					break;
				case 0x2C:
					handleSpawnGlobalEntity(in, bb);
					break;
				case 0x2D:
					handleOpenWindow(in, bb);
					break;
				case 0x2E:
					handleCloseWindow(in, bb);
					break;
				case 0x2F:
					if (!handleSetSlot(in, bb)) {
						bb.release();
						bb = null;
					}
					break;
				case 0x30:
					handleWindowItems(in, bb);
					break;
				case 0x31:
					if (!handleWindowProperty(in, bb)) {
						bb.release();
						bb = null;
					}
					break;
				case 0x32:
					handleConfirmTransaction(in, bb);
					break;
				case 0x33:
					handleUpdateSign(in, bb);
					break;
				case 0x34:
					// handleMap(in, bb);
					bb.release();
					bb = null;
					break;
				case 0x35:
					if (!handleUpdateBlockEntity(in, bb)) {
						bb.release();
						bb = null;
					}
					break;
				case 0x37:
					bb.release();
					bb = null;
					handleStatistics(in, ctx.alloc(), out);
					break;
				case 0x38:
					bb.release();
					bb = null;
					handlePlayerListItem(in, ctx.alloc(), out);
					break;
				case 0x39:
					handlePlayerAbilities(in, bb);
					break;
				case 0x3A:
					handleTabComplete(in, bb);
					break;
				case 0x3B:
					handleScoreboardObjective(in, bb);
					break;
				case 0x3C:
					bb.release();
					bb = null;
					handleUpdateScore(in, ctx.alloc(), out);
					break;
				case 0x3D:
					handleDisplayScoreboard(in, bb);
					break;
				case 0x3E:
					handleTeams(in, bb);
					break;
				case 0x3F:
					handlePluginMessage(in, bb);
					break;
				case 0x40:
					handleDisconnect(in, bb);
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
