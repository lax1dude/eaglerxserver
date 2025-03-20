package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
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

	private byte playerDimension = 0;

	private final Set<Short> enchWindows = new HashSet<>();
	private final Set<Short> furnWindows = new HashSet<>();

	private final Map<Integer, Integer> entityIdToType = new HashMap<>();

	/**
	 * Objects = no offset
	 * Mobs = + 100
	 * 300 = Player
	 * 391 = Painting
	 * 392 = Experience Orb
	 * 393 = Lightning Bolt
	 */

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
		int eid = in.readInt();
		bb.writeInt(eid);
		entityIdToType.put(eid, 300);
		short gamemode = in.readUnsignedByte();
		byte dimension = in.readByte();
		playerDimension = dimension;
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
		BufferUtils.convertSlot2Legacy(in, bb, serverAPI().getNBTHelper());
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
		playerDimension = (byte) in.readInt();
		bb.writeInt(playerDimension);
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
		bb.writeDouble(playerY + 1.6200000047683716D);
		bb.writeDouble(playerY + 1.6200000047683716D);
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

	private ByteBuf handleAnimation(ByteBuf in, ByteBufAllocator alloc) {
		int aeid = BufferUtils.readVarInt(in);
		short animation = in.readUnsignedByte();
		if (animation >= 0 && animation <= 3) {
			ByteBuf bb = alloc.buffer();
			try {
				bb.writeByte(0x12);
				bb.writeInt(aeid);
				bb.writeByte(animation + 1);
				bb.retain();
			} finally {
				bb.release();
			}
			return bb;
		}
		return null;
	}

	private void handleSpawnPlayer(ByteBuf in, ByteBuf bb, ByteBufAllocator alloc) {
		bb.writeByte(0x14);
		int eid = BufferUtils.readVarInt(in);
		bb.writeInt(eid);
		entityIdToType.put(eid, 300);
		UUID uuid = new UUID(in.readLong(), in.readLong());
		ByteBuf tmp = alloc.buffer();
		try {
			tmp.writeInt(in.readInt());
			tmp.writeInt(in.readInt());
			tmp.writeInt(in.readInt());
			tmp.writeByte(in.readByte());
			tmp.writeByte(in.readByte());
			tmp.writeShort(in.readShort());
			String playerName = BufferUtils.convertMetadata2Legacy(in, tmp, 300, alloc, serverAPI().getNBTHelper());
			if (playerName == null) {
				playerName = BufferUtils.getUsernameOrElse(serverAPI(), uuid);
			}
			BufferUtils.writeLegacyMCString(bb, playerName, 255);
			bb.writeBytes(tmp);
		} finally {
			tmp.release();
		}
	}

	private void handleCollectItem(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x16);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeInt(BufferUtils.readVarInt(in));
	}

	private void handleSpawnObject(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x17);
		int eid = BufferUtils.readVarInt(in);
		bb.writeInt(eid);
		int otype = in.readByte();
		bb.writeByte(otype);
		entityIdToType.put(eid, otype);
		int x = in.readInt();
		int y = in.readInt();
		int z = in.readInt();
		int pitch = in.readByte();
		int yaw = in.readByte();
		int odata = in.readInt();
		if (otype == 71) {
			switch (odata) {
				case 0:
					z -= 32;
					yaw = 128;
					break;
				case 1:
					x += 32;
					yaw = 64;
					break;
				case 2:
					z += 32;
					yaw = 0;
					break;
				case 3:
					x -= 32;
					yaw = 192;
					break;
			}
		} else if (otype == 70) {
			int id = BufferUtils.convertType2Legacy(odata & 4095);
			int data = (odata >> 12) & 0xF;
			odata = (id | (data << 16));
		} else if (otype == 50 || otype == 70 || otype == 74) {
			y += 16;
		}
		bb.writeInt(x);
		bb.writeInt(y);
		bb.writeInt(z);
		bb.writeByte(pitch);
		bb.writeByte(yaw);
		bb.writeInt(odata);
		if (odata > 0) {
			bb.writeShort(in.readShort());
			bb.writeShort(in.readShort());
			bb.writeShort(in.readShort());
		}
	}

	private void handleSpawnMob(ByteBuf in, ByteBuf bb, ByteBufAllocator alloc) {
		bb.writeByte(0x18);
		int eid = BufferUtils.readVarInt(in);
		bb.writeInt(eid);
		short mtype = in.readUnsignedByte();
		if (mtype == 67 || mtype == 101) {
			mtype = 60;
		} else if (mtype == 100) {
			mtype = 90;
		} else if (mtype == 68) {
			mtype = 94;
		}
		bb.writeByte(mtype);
		entityIdToType.put(eid, (int) mtype + 100);
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
		BufferUtils.convertMetadata2Legacy(in, bb, mtype + 100, alloc, serverAPI().getNBTHelper());
	}

	private void handleSpawnPainting(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x19);
		int eid = BufferUtils.readVarInt(in);
		bb.writeInt(eid);
		entityIdToType.put(eid, 391);
		BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 255);
		long paintxyz = in.readLong();
		int x = BufferUtils.posX(paintxyz);
		int z = BufferUtils.posZ(paintxyz);
		short dir = in.readUnsignedByte();
		switch (dir) {
			case 0:
				--z;
				break;
			case 1:
				++x;
				break;
			case 2:
				++z;
				break;
			case 3:
				--x;
				break;
		}
		bb.writeInt(x);
		bb.writeInt(BufferUtils.posY(paintxyz));
		bb.writeInt(z);
		bb.writeInt(dir);
	}

	private void handleSpawnExperienceOrb(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x1A);
		int eid = BufferUtils.readVarInt(in);
		bb.writeInt(eid);
		entityIdToType.put(eid, 392);
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
			int eid = BufferUtils.readVarInt(in);
			entityIdToType.remove(eid);
			bb.writeInt(eid);
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
		int eid = BufferUtils.readVarInt(in);
		bb.writeInt(eid);
		int x = in.readInt();
		int y = in.readInt();
		int z = in.readInt();
		int xd = entityIdToType.getOrDefault(eid, -1);
		if (xd != -1 && (xd == 50 || xd == 70 || xd == 74)) {
			y += 16;
		}
		bb.writeInt(x);
		bb.writeInt(y);
		bb.writeInt(z);
		bb.writeByte(in.readByte());
		bb.writeByte(in.readByte());
	}

	private void handleEntityHeadLook(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x23);
		bb.writeInt(BufferUtils.readVarInt(in));
		bb.writeByte(in.readByte());
	}

	private ByteBuf handleEntityStatus(ByteBuf in, ByteBufAllocator alloc) {
		// todo: make sure invalid entity statuses dont make 1.5 explode, e.g. guardian stuff+
		int i = in.readInt();
		byte b = in.readByte();
		if (b <= 17) {
			ByteBuf bb = alloc.buffer();
			try {
				bb.writeByte(0x26);
				bb.writeInt(i);
				bb.writeByte(b);
				bb.retain();
			} finally {
				bb.release();
			}
			return bb;
		}
		return null;
	}

	private void handleAttachEntity(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x27);
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
	}

	private void handleEntityMetadata(ByteBuf in, ByteBuf bb, ByteBufAllocator alloc) {
		bb.writeByte(0x28);
		int eid = BufferUtils.readVarInt(in);
		bb.writeInt(eid);
		BufferUtils.convertMetadata2Legacy(in, bb, entityIdToType.getOrDefault(eid, -1), alloc, serverAPI().getNBTHelper());
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

	private void handleChunkData(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x33);
		int chunkX = in.readInt();
		int chunkZ = in.readInt();
		boolean chunkCont = in.readBoolean();
		int chunkPbm = in.readUnsignedShort();
		BufferUtils.readVarInt(in);
		int aaaa = bb.writerIndex();
		bb.writerIndex(aaaa + 17);
		int size = BufferUtils.calcChunkDataSize(Integer.bitCount(chunkPbm), playerDimension == 0, chunkCont);
		BufferUtils.convertChunk2Legacy(chunkPbm, size, in, bb);
		bb.setInt(aaaa, chunkX);
		aaaa += 4;
		bb.setInt(aaaa, chunkZ);
		aaaa += 4;
		bb.setBoolean(aaaa, chunkCont);
		aaaa += 1;
		bb.setShort(aaaa, chunkPbm);
		aaaa += 2;
		bb.setShort(aaaa, 0);
		aaaa += 2;
		bb.setInt(aaaa, (bb.writerIndex() - (aaaa + 4)) | 0x10000000);
		// aaaa += 4;
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

	private void handleMapChunkBulk(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x38);
		boolean mcbSkyLightSent = in.readBoolean();
		int mcbCcc = BufferUtils.readVarInt(in);
		int[] chunkX = new int[mcbCcc];
		int[] chunkZ = new int[mcbCcc];
		int[] bitmap = new int[mcbCcc];
		for (int ii = 0; ii < mcbCcc; ++ii) {
			chunkX[ii] = in.readInt();
			chunkZ[ii] = in.readInt();
			bitmap[ii] = in.readUnsignedShort();
		}
		int aaaa = bb.writerIndex();
		bb.writerIndex(aaaa + 7);
		for (int ii = 0; ii < mcbCcc; ++ii) {
			int size = BufferUtils.calcChunkDataSize(Integer.bitCount(bitmap[ii]), mcbSkyLightSent, true);
			BufferUtils.convertChunk2Legacy(bitmap[ii], size, in, bb);
		}
		bb.setShort(aaaa, mcbCcc);
		aaaa += 2;
		bb.setInt(aaaa, (bb.writerIndex() - (aaaa + (4 + 1))) | 0x10000000);
		aaaa += 4;
		bb.setBoolean(aaaa, mcbSkyLightSent);
		// aaaa += 1;
		for (int ii = 0; ii < mcbCcc; ++ii) {
			bb.writeInt(chunkX[ii]);
			bb.writeInt(chunkZ[ii]);
			bb.writeShort(bitmap[ii]);
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
		String sound = BufferUtils.readMCString(in, 255);
		switch (sound) {
			case "game.player.hurt.fall.big":
			case "game.neutral.hurt.fall.big":
			case "game.hostile.hurt.fall.big":
				sound = "damage.fallbig";
				break;
			case "game.player.hurt.fall.small":
			case "game.neutral.hurt.fall.small":
			case "game.hostile.hurt.fall.small":
				sound = "damage.fallsmall";
				break;
			case "game.player.hurt":
			case "game.player.die":
			case "game.neutral.hurt":
			case "game.neutral.die":
			case "game.hostile.hurt":
			case "game.hostile.die":
				sound = "damage.hit";
				break;
			case "game.player.swim":
			case "game.neutral.swim":
			case "game.hostile.swim":
				sound = "liquid.swim";
				break;
			case "game.player.swim.splash":
			case "game.neutral.swim.splash":
			case "game.hostile.swim.splash":
				sound = "liquid.splash";
				break;
		}
		BufferUtils.writeLegacyMCString(bb, sound, 255);
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeInt(in.readInt());
		bb.writeFloat(in.readFloat());
		bb.writeByte(in.readUnsignedByte());
	}

	private ByteBuf handleParticle(ByteBuf in, ByteBufAllocator alloc) {
		int pId = in.readInt();
		String pName = particleNames[pId];
		if (!pName.isEmpty()) {
			ByteBuf bb = alloc.buffer();
			try {
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
				bb.retain();
			} finally {
				bb.release();
			}
			return bb;
		}
		return null;
	}

	private ByteBuf handleChangeGameState(ByteBuf in, ByteBufAllocator alloc) {
		short cgsReason = in.readUnsignedByte();
		if (cgsReason >= 0 && cgsReason <= 4) {
			ByteBuf bb = alloc.buffer();
			try {
				bb.writeByte(0x46);
				bb.writeByte(cgsReason);
				float cgsValue = in.readFloat();
				if (cgsValue > 1) cgsValue = 0;
				bb.writeByte((int) cgsValue);
				bb.retain();
			} finally {
				bb.release();
			}
			return bb;
		}
		return null;
	}

	private void handleSpawnGlobalEntity(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0x47);
		int eid = BufferUtils.readVarInt(in);
		bb.writeInt(eid);
		entityIdToType.put(eid, 393);
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
			case "minecraft:container":
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
			case "minecraft:dropper":
				windowId = 10;
				break;
			case "EntityHorse": {
				windowId = 11;
				break;
			}
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

	private ByteBuf handleSetSlot(ByteBuf in, ByteBufAllocator alloc) {
		ByteBuf bb = alloc.buffer();
		try {
			bb.writeByte(0x67);
			byte windowUniqueId = in.readByte();
			bb.writeByte(windowUniqueId);
			short slot = in.readShort();
			if (enchWindows.contains((short) windowUniqueId) && slot > 0) {
				if (slot == 1) {
					return null;
				}
				bb.writeShort(slot - 1);
			} else {
				bb.writeShort(slot);
			}
			BufferUtils.convertSlot2Legacy(in, bb, serverAPI().getNBTHelper());
			bb.retain();
		} finally {
			bb.release();
		}
		return bb;
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
			if (!in.isReadable()) {
				bb.clear();
				return;
			}
			if (ench && ii == 1) {
				int here = bb.writerIndex();
				BufferUtils.convertSlot2Legacy(in, bb, serverAPI().getNBTHelper());
				bb.writerIndex(here);
			} else {
				BufferUtils.convertSlot2Legacy(in, bb, serverAPI().getNBTHelper());
			}
		}
	}

	private ByteBuf handleWindowProperty(ByteBuf in, ByteBufAllocator alloc) {
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
			ByteBuf bb = alloc.buffer();
			try {
				bb.writeByte(0x69);
				bb.writeByte(grah);
				bb.writeShort(uwpProp);
				bb.writeShort(in.readShort());
				bb.retain();
			} finally {
				bb.release();
			}
			return bb;
		}
		return null;
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
			BufferUtils.writeLegacyMCString(bb, serverAPI().getComponentHelper().convertJSONToLegacySection(BufferUtils.readMCString(in, 4095)), 15);
		}
	}

	private void handleMap(ByteBuf in, ByteBufAllocator alloc, List<Object> out) {
		int mapId = BufferUtils.readVarInt(in);
		in.skipBytes(1);
		int iconNum = BufferUtils.readVarInt(in);
		if (iconNum > 0) {
			ByteBuf bb = alloc.buffer();
			try {
				bb.writeByte(0x83);
				bb.writeShort(358);
				bb.writeShort(mapId);
				bb.writeShort(3 * iconNum + 1);
				bb.writeByte((byte) 1);
				bb.writeBytes(in, 3 * iconNum);
				out.add(bb.retain());
			} finally {
				bb.release();
			}
		}
		short columns = in.readUnsignedByte();
		if (columns == 0) {
			return;
		}
		short rows = in.readUnsignedByte();
		short xstart = in.readUnsignedByte();
		short zstart = in.readUnsignedByte();
		/*int dataLen = */BufferUtils.readVarInt(in);

		int absInd = in.readerIndex();
		ByteBuf colors = alloc.buffer(16384);
		try {
			int columnStart;
			int columnEnd;
			int rowStart;
			int rowEnd;
			for (int column = 0; column < columns; ++column) {
				for (int row = 0; row < rows; ++row) {
					colors.setByte(xstart + column + (zstart + row) * 128, in.getByte(absInd + column + row * columns));
				}
			}
			columnStart = xstart;
			columnEnd = xstart + columns;
			rowStart = zstart;
			rowEnd = zstart + rows;

			int theGuh = rowEnd - rowStart;

			for (int column = columnStart; column < columnEnd; column++) {
				ByteBuf bb = alloc.buffer();
				try {
					bb.writeByte(0x83);
					bb.writeShort(358);
					bb.writeShort(mapId);
					bb.writeShort(3 + theGuh);
					bb.writeByte((byte) 0);
					bb.writeByte(column);
					bb.writeByte(rowStart);
					bb.ensureWritable(theGuh);
					int absWInd = bb.writerIndex();
					for (int row = rowStart; row < rowEnd; row++) {
						bb.setByte(absWInd + (row - rowStart), BufferUtils.convertMapColor2Legacy(colors.getByte(row * 128 + column)));
					}
					bb.writerIndex(absWInd + theGuh);
					out.add(bb.retain());
				} finally {
					bb.release();
				}
			}
		} finally {
			colors.release();
		}

		// in.skipBytes(dataLen);
	}

	private ByteBuf handleUpdateBlockEntity(ByteBuf in, ByteBufAllocator alloc) {
		long ubePos = in.readLong();
		short ubeAct = in.readShort();
		if (ubeAct == 1 || ubeAct == 3 || ubeAct == 4 || ubeAct == 5) {
			ByteBuf bb = alloc.buffer();
			try {
				bb.writeByte(0x84);
				bb.writeInt(BufferUtils.posX(ubePos));
				bb.writeShort(BufferUtils.posY(ubePos));
				bb.writeInt(BufferUtils.posZ(ubePos));
				bb.writeByte(ubeAct);
				BufferUtils.convertNBT2Legacy(in, bb, serverAPI().getNBTHelper());
				bb.retain();
			} finally {
				bb.release();
			}
			return bb;
		}
		return null;
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
							BufferUtils.readMCString(in, Integer.MAX_VALUE);
							if (in.readBoolean()) {
								BufferUtils.readMCString(in, Integer.MAX_VALUE);
							}
						}
						BufferUtils.readVarInt(in);
						int tbPing = BufferUtils.readVarInt(in);
						if (in.readBoolean()) {
							tempName = serverAPI().getComponentHelper().convertJSONToLegacySection(BufferUtils.readMCString(in, 32767));
						}
						tabList.put(pliUuid, new TabListItem(tempName, tbPing));
					} else {
						tabList.put(pliUuid, new TabListItem(BufferUtils.getUsernameOrElse(serverAPI(), pliUuid), 0));
					}
				}
				TabListItem pliItem = tabList.get(pliUuid);
				if (pliAction != 0) {
					bb = alloc.buffer();
					try {
						bb.writeByte(0xC9);
						BufferUtils.writeLegacyMCString(bb, pliItem.name, 16);
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
						BufferUtils.writeLegacyMCString(bb, pliItem.name, 16);
						bb.writeBoolean(true);
						bb.writeShort(pliItem.ping = BufferUtils.readVarInt(in));
						out.add(bb.retain());
					} finally {
						bb.release();
					}
				} else if (pliAction == 3) {
					if (in.readBoolean()) {
						pliItem.name = serverAPI().getComponentHelper().convertJSONToLegacySection(BufferUtils.readMCString(in, 32767));
					}
				}
				if (pliAction != 4) {
					bb = alloc.buffer();
					try {
						bb.writeByte(0xC9);
						BufferUtils.writeLegacyMCString(bb, pliItem.name, 16);
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
		float speed1 = in.readFloat() * 256;
		float speed2 = in.readFloat() * 256;
		bb.writeByte((int) speed1);
		bb.writeByte((int) speed2);
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
					BufferUtils.writeLegacyMCString(bb, sbItem, 16);
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
							BufferUtils.writeLegacyMCString(bb, sbItem, 16);
							bb.writeByte(0);
							BufferUtils.writeLegacyMCString(bb, s, 16);
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
					BufferUtils.writeLegacyMCString(bb, sbItem, 16);
					bb.writeByte(usAction);
					BufferUtils.writeLegacyMCString(bb, sbName, 16);
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
		short b = in.readUnsignedByte();
		if (b > 3) {
			bb.clear();
			return;
		}
		bb.writeByte(b);
		BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 16);
	}

	private void handleTeams(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0xD1);
		BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 16);
		short teamMode = in.readUnsignedByte();
		bb.writeByte(teamMode);
		if (teamMode == 0 || teamMode == 2) {
			BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 32);
			BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 16);
			BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 16);
			bb.writeByte(in.readByte());
			BufferUtils.readMCString(in, 255);
			in.readByte(); // team color, does not exist in 1.5, maybe fake it by appending to display name, prefix, and suffix???
		}
		if (teamMode == 0 || teamMode == 3 || teamMode == 4) {
			int teamPlNum = BufferUtils.readVarInt(in);
			bb.writeShort(teamPlNum);
			for (int ii = 0; ii < teamPlNum; ++ii) {
				BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(in, 255), 16);
			}
		}
	}

	private ByteBuf handlePluginMessage(ByteBuf in, ByteBufAllocator alloc) {
		String name = BufferUtils.readMCString(in, 255);
		switch (name) {
			case "MC|TPack":
				return null;
			case "MC|TrList":
				int ri = in.readerIndex();
				in.skipBytes(4);
				short count = in.readUnsignedByte();
				ByteBuf tmp = alloc.buffer();
				try {
					for (int i = 0; i < count; ++i) {
						BufferUtils.convertSlot2Legacy(in, tmp, serverAPI().getNBTHelper());
						BufferUtils.convertSlot2Legacy(in, tmp, serverAPI().getNBTHelper());
						boolean guh = in.readBoolean();
						tmp.writeBoolean(guh);
						if (guh) {
							BufferUtils.convertSlot2Legacy(in, tmp, serverAPI().getNBTHelper());
						}
						tmp.writeBoolean(in.readBoolean());
						in.skipBytes(8);
					}
					in.readerIndex(ri);
					in.writerIndex(ri + 5);
					in.writeBytes(tmp);
				} finally {
					tmp.release();
				}
				break;
		}
		ByteBuf bb = alloc.buffer();
		try {
			bb.writeByte(0xFA);
			BufferUtils.writeLegacyMCString(bb, name, 255);
			int pmLen = in.readableBytes();
			bb.writeShort(pmLen);
			bb.writeBytes(in, pmLen);
			return bb.retain();
		} finally {
			bb.release();
		}
	}

	private void handleDisconnect(ByteBuf in, ByteBuf bb) {
		bb.writeByte(0xFF);
		BufferUtils.writeLegacyMCString(bb, serverAPI().getComponentHelper().convertJSONToLegacySection(BufferUtils.readMCString(in, 32767)), 32767);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int pktId = BufferUtils.readVarInt(in);
		ByteBuf bb = null;
		try {
			switch (pktId) {
				case 0x00:
					bb = ctx.alloc().buffer();
					handleKeepAlive(in, bb);
					break;
				case 0x01:
					bb = ctx.alloc().buffer();
					handleJoinGame(in, bb);
					break;
				case 0x02:
					bb = ctx.alloc().buffer();
					handleChatMessage(in, bb);
					break;
				case 0x03:
					bb = ctx.alloc().buffer();
					handleTimeUpdate(in, bb);
					break;
				case 0x04:
					bb = ctx.alloc().buffer();
					handleEntityEquipment(in, bb);
					break;
				case 0x05:
					bb = ctx.alloc().buffer();
					handleSpawnPosition(in, bb);
					break;
				case 0x06:
					bb = ctx.alloc().buffer();
					handleUpdateHealth(in, bb);
					break;
				case 0x07:
					bb = ctx.alloc().buffer();
					handleRespawn(in, bb);
					break;
				case 0x08:
					bb = ctx.alloc().buffer();
					handlePlayerPositionAndLook(in, bb);
					break;
				case 0x09:
					bb = ctx.alloc().buffer();
					handleHeldItemChange(in, bb);
					break;
				case 0x0A:
					bb = ctx.alloc().buffer();
					handleUseBed(in, bb);
					break;
				case 0x0B:
					bb = handleAnimation(in, ctx.alloc());
					break;
				case 0x0C:
					bb = ctx.alloc().buffer();
					handleSpawnPlayer(in, bb, ctx.alloc());
					break;
				case 0x0D:
					bb = ctx.alloc().buffer();
					handleCollectItem(in, bb);
					break;
				case 0x0E:
					bb = ctx.alloc().buffer();
					handleSpawnObject(in, bb);
					break;
				case 0x0F:
					bb = ctx.alloc().buffer();
					handleSpawnMob(in, bb, ctx.alloc());
					break;
				case 0x10:
					bb = ctx.alloc().buffer();
					handleSpawnPainting(in, bb);
					break;
				case 0x11:
					bb = ctx.alloc().buffer();
					handleSpawnExperienceOrb(in, bb);
					break;
				case 0x12:
					bb = ctx.alloc().buffer();
					handleEntityVelocity(in, bb);
					break;
				case 0x13:
					bb = ctx.alloc().buffer();
					handleDestroyEntities(in, bb);
					break;
				case 0x14:
					bb = ctx.alloc().buffer();
					handleEntity(in, bb);
					break;
				case 0x15:
					bb = ctx.alloc().buffer();
					handleEntityRelativeMove(in, bb);
					break;
				case 0x16:
					bb = ctx.alloc().buffer();
					handleEntityLook(in, bb);
					break;
				case 0x17:
					bb = ctx.alloc().buffer();
					handleEntityLookAndRelativeMove(in, bb);
					break;
				case 0x18:
					bb = ctx.alloc().buffer();
					handleEntityTeleport(in, bb);
					break;
				case 0x19:
					bb = ctx.alloc().buffer();
					handleEntityHeadLook(in, bb);
					break;
				case 0x1A:
					bb = handleEntityStatus(in, ctx.alloc());
					break;
				case 0x1B:
					bb = ctx.alloc().buffer();
					handleAttachEntity(in, bb);
					break;
				case 0x1C:
					bb = ctx.alloc().buffer();
					handleEntityMetadata(in, bb, ctx.alloc());
					break;
				case 0x1D:
					bb = ctx.alloc().buffer();
					handleEntityEffect(in, bb);
					break;
				case 0x1E:
					bb = ctx.alloc().buffer();
					handleRemoveEntityEffect(in, bb);
					break;
				case 0x1F:
					bb = ctx.alloc().buffer();
					handleSetExperience(in, bb);
					break;
				case 0x21:
					bb = ctx.alloc().buffer();
					handleChunkData(in, bb);
					break;
				case 0x22:
					bb = ctx.alloc().buffer();
					handleMultiBlockChange(in, bb);
					break;
				case 0x23:
					bb = ctx.alloc().buffer();
					handleBlockChange(in, bb);
					break;
				case 0x24:
					bb = ctx.alloc().buffer();
					handleBlockAction(in, bb);
					break;
				case 0x25:
					bb = ctx.alloc().buffer();
					handleBlockBreakAnimation(in, bb);
					break;
				case 0x26:
					bb = ctx.alloc().buffer();
					handleMapChunkBulk(in, bb);
					break;
				case 0x27:
					bb = ctx.alloc().buffer();
					handleExplosion(in, bb);
					break;
				case 0x28:
					bb = ctx.alloc().buffer();
					handleEffect(in, bb);
					break;
				case 0x29:
					bb = ctx.alloc().buffer();
					handleSoundEffect(in, bb);
					break;
				case 0x2A:
					bb = handleParticle(in, ctx.alloc());
					break;
				case 0x2B:
					bb = handleChangeGameState(in, ctx.alloc());
					break;
				case 0x2C:
					bb = ctx.alloc().buffer();
					handleSpawnGlobalEntity(in, bb);
					break;
				case 0x2D:
					bb = ctx.alloc().buffer();
					handleOpenWindow(in, bb);
					break;
				case 0x2E:
					bb = ctx.alloc().buffer();
					handleCloseWindow(in, bb);
					break;
				case 0x2F:
					bb = handleSetSlot(in, ctx.alloc());
					break;
				case 0x30:
					bb = ctx.alloc().buffer();
					handleWindowItems(in, bb);
					break;
				case 0x31:
					bb = handleWindowProperty(in, ctx.alloc());
					break;
				case 0x32:
					bb = ctx.alloc().buffer();
					handleConfirmTransaction(in, bb);
					break;
				case 0x33:
					bb = ctx.alloc().buffer();
					handleUpdateSign(in, bb);
					break;
				case 0x34:
					handleMap(in, ctx.alloc(), out);
					break;
				case 0x35:
					bb = handleUpdateBlockEntity(in, ctx.alloc());
					break;
				case 0x37:
					handleStatistics(in, ctx.alloc(), out);
					break;
				case 0x38:
					handlePlayerListItem(in, ctx.alloc(), out);
					break;
				case 0x39:
					bb = ctx.alloc().buffer();
					handlePlayerAbilities(in, bb);
					break;
				case 0x3A:
					bb = ctx.alloc().buffer();
					handleTabComplete(in, bb);
					break;
				case 0x3B:
					bb = ctx.alloc().buffer();
					handleScoreboardObjective(in, bb);
					break;
				case 0x3C:
					handleUpdateScore(in, ctx.alloc(), out);
					break;
				case 0x3D:
					bb = ctx.alloc().buffer();
					handleDisplayScoreboard(in, bb);
					break;
				case 0x3E:
					bb = ctx.alloc().buffer();
					handleTeams(in, bb);
					break;
				case 0x3F:
					bb = handlePluginMessage(in, ctx.alloc());
					break;
				case 0x40:
					bb = ctx.alloc().buffer();
					handleDisconnect(in, bb);
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
