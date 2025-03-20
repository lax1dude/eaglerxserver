package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.lax1dude.eaglercraft.backend.server.api.IBasePlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public class BufferUtils {

	public static int readVarInt(ByteBuf buffer) {
		return BufferUtils.readVarInt(buffer, 5);
	}

	public static int readVarInt(ByteBuf buffer, int maxBytes) {
		int out = 0;
		int bytes = 0;
		byte in;
		while (true) {
			in = buffer.readByte();

			out |= (in & 0x7F) << (bytes++ * 7);

			if (bytes > maxBytes) {
				throw new IndexOutOfBoundsException("VarInt too big (max " + maxBytes + ")");
			}

			if ((in & 0x80) != 0x80) {
				break;
			}
		}

		return out;
	}

	public static long readVarLong(ByteBuf buffer, int maxBytes) {
		long i = 0L;
		int j = 0;
		byte b0;
		while (true) {
			b0 = buffer.readByte();

			i |= (long) (b0 & 0x7F) << j++ * 7;

			if (j > maxBytes) {
				throw new IndexOutOfBoundsException("VarLong too big (max " + maxBytes + ")");
			}

			if ((b0 & 0x80) != 0x80) {
				break;
			}
		}

		return i;
	}

	public static void writeVarInt(ByteBuf buffer, int input) {
		while ((input & -128) != 0) {
			buffer.writeByte(input & 127 | 128);
			input >>>= 7;
		}

		buffer.writeByte(input);
	}

	public static void writeVarLong(ByteBuf buffer, long value) {
		while ((value & -128L) != 0L) {
			buffer.writeByte((int) (value & 127L) | 128);
			value >>>= 7;
		}

		buffer.writeByte((int) value);
	}

	public static int varIntLength(int val) {
		for (int i = 1; i < 5; ++i) {
			if ((val & -1 << i * 7) == 0) {
				return i;
			}
		}

		return 5;
	}

	public static String readLegacyMCString(ByteBuf buffer, int maxLen) {
		int len = buffer.readUnsignedShort();
		if(len > maxLen) {
			throw new IndexOutOfBoundsException("String too long");
		}
		char[] chars = new char[len];
		for(int i = 0; i < len; ++i) {
			chars[i] = buffer.readChar();
		}
		return new String(chars);
	}

	public static void writeLegacyMCString(ByteBuf buffer, String value, int maxLen) {
		int len = value.length();
		if(len > maxLen) {
			value = value.substring(0, maxLen);
			len = maxLen;
			// throw new IndexOutOfBoundsException();
		}
		buffer.writeShort(len);
		for(int i = 0; i < len; ++i) {
			buffer.writeChar(value.charAt(i));
		}
	}

	public static String readMCString(ByteBuf buffer, int maxLen) {
		int len = BufferUtils.readVarInt(buffer);
		if(len * 4 > maxLen) {
			throw new IndexOutOfBoundsException();
		}
		CharSequence ret = buffer.readCharSequence(len, StandardCharsets.UTF_8);
		if(ret.length() > maxLen) {
			throw new IndexOutOfBoundsException();
		}
		return ret.toString();
	}

	public static void writeMCString(ByteBuf buffer, String value, int maxLen) {
		if(value.length() > maxLen) {
			throw new IndexOutOfBoundsException();
		}
		byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		BufferUtils.writeVarInt(buffer, bytes.length);
		buffer.writeBytes(bytes);
	}

	public static void convertSlot2Legacy(ByteBuf buffer, ByteBuf bb) {
		short blockId = buffer.readShort();
		blockId = (short) convertItem2Legacy(blockId);
		bb.writeShort(blockId);
		if (blockId == -1) {
			return;
		}
		byte itemCount = buffer.readByte();
		short itemDamage = buffer.readShort();
		bb.writeByte(itemCount);
		bb.writeShort(itemDamage);
		convertNBT2Legacy(buffer, bb);
	}

	public static void convertLegacySlot(ByteBuf buffer, ByteBuf bb) {
		short blockId = buffer.readShort();
		bb.writeShort(blockId);
		if (blockId == -1) {
			return;
		}
		byte itemCount = buffer.readByte();
		short itemDamage = buffer.readShort();
		bb.writeByte(itemCount);
		bb.writeShort(itemDamage);
		convertLegacyNBT(buffer, bb);
	}

	public static void convertNBT2Legacy(ByteBuf buffer, ByteBuf bb) {
		if (buffer.readUnsignedByte() == 0) {
			bb.writeShort(-1);
			return;
		}
		buffer.readerIndex(buffer.readerIndex() - 1);
		byte[] inputBytes = new byte[buffer.readableBytes()];
		buffer.readBytes(inputBytes);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 GZIPOutputStream gzipOs = new GZIPOutputStream(baos)) {

			gzipOs.write(inputBytes);
			gzipOs.finish();

			byte[] compressedBytes = baos.toByteArray();

			bb.writeShort(compressedBytes.length);
			bb.writeBytes(compressedBytes);
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
	}

	public static void convertLegacyNBT(ByteBuf buffer, ByteBuf bb) {
		short len1 = buffer.readShort();
		if (len1 == -1) {
			bb.writeByte(0);
			return;
		}
		byte[] compressedBytes = new byte[len1];
		buffer.readBytes(compressedBytes);
		try (ByteArrayInputStream bais = new ByteArrayInputStream(compressedBytes);
			 GZIPInputStream gzipIs = new GZIPInputStream(bais);
			 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			byte[] buf = new byte[1024];
			int len;
			while ((len = gzipIs.read(buf)) > 0) {
				baos.write(buf, 0, len);
			}

			byte[] decompressedBytes = baos.toByteArray();

			bb.writeBytes(decompressedBytes);
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
	}

	public static int calcChunkDataSize(final int count, final boolean light, final boolean sendBiomes) {
		int idlength = count * 2 * 16 * 16 * 16;
		int blocklightlength = (count * 16 * 16 * 16) / 2;
		int skylightlength = light ? ((count * 16 * 16 * 16) / 2) : 0;
		int biomeslength = sendBiomes ? 256 : 0;
		return idlength + blocklightlength + skylightlength + biomeslength;
	}

	public static void convertChunk2Legacy(int bitmap, int data18len, ByteBuf data18, ByteBuf bb) {
		int absInd = data18.readerIndex();
		int absWInd = bb.writerIndex();
		int count = Integer.bitCount(bitmap);
		int guh1 = 8192 * count;
		int guh = data18len - guh1;
		int guh2 = count * (4096 + 2048);
		bb.ensureWritable(guh2 + guh);

		for (int i = 0; i < (8192 * count); i += 4) {
			int stateA = data18.getUnsignedShortLE(absInd + i);
			int stateB = data18.getUnsignedShortLE(absInd + i + 2);

			bb.setShortLE(absWInd + (i >> 1), convertType2Legacy(stateA >> 4) | (convertType2Legacy(stateB >> 4) << 8));
			bb.setByte(absWInd + count * 4096 + (i >> 2), (byte)((stateA & 0xF) | ((stateB & 0xF) << 4)));
		}

		if (guh == 256 && data18.readableBytes() - (absInd + guh1) < 256) {
			bb.setZero(absWInd + guh2, 256);
			data18.skipBytes(data18len - 256);
		} else {
			data18.getBytes(absInd + guh1, bb, absWInd + guh2, guh);
			data18.skipBytes(data18len);
		}
		bb.writerIndex(absWInd + guh2 + guh);
	}

	public static int posX(long position) {
		return (int)(position >> 38);
	}

	public static int posY(long position) {
		return (int)((position >> 26) & 0xFFF);
	}

	public static int posZ(long position) {
		return (int)(position << 38 >> 38);
	}

	public static long createPosition(int x, int y, int z) {
		return ((long) (x & 0x3FFFFFF) << 38) | ((long) (y & 0xFFF) << 26) | (z & 0x3FFFFFF);
	}

	public static String convertMetadata2Legacy(ByteBuf buffer, ByteBuf bb, int entityType, ByteBufAllocator alloc) {
		String playerNameWowie = null;

		if (entityType == -1) {
			bb.writeByte(0x7F);
			return null;
		}
		while (true) {
			int item = buffer.readUnsignedByte();
			if (item == 0x7F) {
				bb.writeByte(item);
				break;
			}
			int index = item & 0x1F;
			int type = item >> 5;
			if (type == 7) {
				buffer.readFloat();
				buffer.readFloat();
				buffer.readFloat();
				continue;
			}
			int ind = bb.writerIndex();
			switch (type) {
				case 0:
					remapMeta(entityType, index, type, bb, buffer.readByte());
					break;
				case 1:
					remapMeta(entityType, index, type, bb, buffer.readShort());
					break;
				case 2:
					remapMeta(entityType, index, type, bb, buffer.readInt());
					break;
				case 3:
					remapMeta(entityType, index, type, bb, buffer.readFloat());
					break;
				case 4:
					PlayerNameHolder playerNameHolder = entityType == 300 ? new PlayerNameHolder() : null;
					remapMeta(entityType, index, type, bb, BufferUtils.readMCString(buffer, 32767), playerNameHolder);
					if (playerNameHolder != null) {
						playerNameWowie = playerNameHolder.name;
					}
					break;
				case 5:
					ByteBuf tmp = alloc.buffer();
					try {
						BufferUtils.convertSlot2Legacy(buffer, tmp);
						remapMeta(entityType, index, type, bb, tmp);
					} finally {
						tmp.release();
					}
					break;
				case 6:
					remapMeta(entityType, index, type, bb, new int[] {
							buffer.readInt(),
							buffer.readInt(),
							buffer.readInt()
					});
					break;
			}
		}
		if (bb.getByte(bb.writerIndex() - 1) != 0x7F) {
			bb.writeByte(0x7F);
		}

		return playerNameWowie;
	}

	private static final class PlayerNameHolder {
		public String name;
	}

	private static void remapMeta(int entityType, int index, int entryType, ByteBuf bb, Object entryValue) {
		remapMeta(entityType, index, entryType, bb, entryValue, null);
	}

	private static void remapMeta(int entityType, int index, int entryType, ByteBuf bb, Object entryValue, PlayerNameHolder playerNameHolder) {
		boolean mobNotObject = entityType >= 100 && entityType <= 300;
		if (entityType >= 300) {
			entityType -= 300;
		} else if (entityType >= 100) {
			entityType -= 100;
		}
		/**
		 * If Object:
		 * 91 = Painting
		 * 92 = Experience orb
		 * 93 = Lightning Bolt
		 * If Mob:
		 * 0 = Player
		 * All else follows wiki.vg
		 */
		if (index == 2 && entryType == 4) {
			index = 5;
		} else if (index == 3 && entryType == 0) {
			index = 6;
		} else if (index == 7 && entryType == 2) {
			index = 8;
		} else if (index == 8 && entryType == 0) {
			index = 9;
		} else if (!mobNotObject && entityType != 71 && (index == 9 || index == 15) && entryType == 0) {
			return;
		} else if (entityType != 54 && index == 12 && entryType == 0) {
			entryType = 2;
			entryValue = (int) (byte) entryValue;
		} else if (mobNotObject) {
			if (entityType == 0 && index == 9 && entryType == 0) {
				index = 10;
			} else if (entityType == 0 && (((index == 10 || index == 16) && entryType == 0) || (index == 17 && entryType == 3))) {
				return;
			} else if (entityType == 0 && index == 18 && entryType == 2) {
				return;
			} else if (entityType == 0 && index == 5 && entryType == 4 && playerNameHolder != null) {
				playerNameHolder.name = (String) entryValue;
			} else if (entityType == 54 && index == 14 && entryType == 0) {
				return;
			} else if (entityType == 58 && index == 16 && entryType == 1) {
				entryType = 0;
				entryValue = (byte) (short) entryValue;
			} else if (entityType == 60 || entityType == 94) {
				return;
			} else if ((entityType == 63 || entityType == 64) && index == 6 && entryType == 3) {
				index = 16;
				entryType = 2;
				entryValue = (int) (float) entryValue;
			} else if (entityType == 66 && index == 21 && entryType == 0) {
				return;
			} else if (entityType == 95 && (index == 18 || index == 6) && entryType == 3) {
				index = 18;
				entryType = 2;
				entryValue = (int) (float) entryValue;
			} else if (index == 16 && entryType == 2) {
				entryType = 0;
				entryValue = (byte) (int) entryValue;
			}
		} else {
			if ((entityType == 1 || entityType == 10 || entityType == 11 || entityType == 12) && index == 19 && entryType == 3) {
				entryType = 2;
				entryValue = (int) (float) entryValue;
			} else if (entityType == 51 && index == 8 && entryType == 2) {
				return;
			} else if (entityType == 60 && index == 16 && entryType == 0) {
				entryValue = (byte) 0;
			} else if (entityType == 71 && index == 8 && entryType == 5) {
				index = 2;
			} else if (entityType == 71 && index == 9 && entryType == 0) {
				index = 3;
				entryValue = (byte) (((byte) entryValue) / 2);
			} else if (entityType == 77 || entityType == 78 || entityType == 90) {
				return;
			}
		}

		bb.writeByte((entryType << 5) | index);
		switch (entryType) {
			case 0:
				bb.writeByte((byte) entryValue);
				break;
			case 1:
				bb.writeShort((short) entryValue);
				break;
			case 2:
				bb.writeInt((int) entryValue);
				break;
			case 3:
				bb.writeFloat((float) entryValue);
				break;
			case 4:
				BufferUtils.writeLegacyMCString(bb, (String) entryValue, 64);
				break;
			case 5:
				bb.writeBytes((ByteBuf) entryValue);
				break;
			case 6:
				int[] fard = (int[]) entryValue;
				bb.writeInt(fard[0]);
				bb.writeInt(fard[1]);
				bb.writeInt(fard[2]);
				break;
		}
	}

	public static int convertItem2Legacy(int item) {
		item = convertType2Legacy(item);
		switch (item) {
			case 409:
				return 318;
			case 410:
				return 289;
			case 411:
				return 365;
			case 412:
			case 423:
			case 424:
				return 366;
			case 413:
				return 282;
			case 414:
				return 376;
			case 415:
				return 334;
			case 416:
			case 420:
			case 421:
				return 280;
			case 425:
				return 323;
			case 427:
			case 428:
			case 429:
			case 430:
			case 431:
				return 324;
			case 422:
				return 328;
			case 417:
			case 418:
			case 419:
				return 329;
		}
		return item;
	}

	public static int convertType2Legacy(int type) {
		switch (type) {
			case 165:
				return 133;
			case 166:
			case 95:
				return 20;
			case 167:
				return 96;
			case 168:
				return 48;
			case 169:
				return 89;
			case 176:
				return 63;
			case 177:
				return 68;
			case 179:
				return 24;
			case 180:
				return 128;
			case 181:
				return 43;
			case 182:
				return 44;
			case 183:
			case 184:
			case 185:
			case 186:
			case 187:
				return 107;
			case 188:
			case 189:
			case 190:
			case 191:
			case 192:
				return 85;
			case 193:
			case 194:
			case 195:
			case 196:
			case 197:
				return 64;
			case 178:
				return 151;
			case 160:
				return 102;
			case 161:
				return 18;
			case 162:
				return 17;
			case 163:
			case 164:
				return 53;
			case 174:
				return 80;
			case 175:
				return 38;
			case 159:
				return 82;
			case 170:
				return 1;
			case 171:
				return 70;
			case 172:
				return 82;
			case 173:
				return 1;
		}
		return type;
	}

	public static byte convertMapColor2Legacy(byte color) {
		int realColor = (color & 0xFF) >> 2;
		switch (realColor) {
			case 14:
				realColor = 8;
				break;
			case 15:
			case 26:
			case 34:
			case 36:
				realColor = 10;
				break;
			case 16:
			case 17:
			case 23:
			case 24:
			case 25:
			case 31:
			case 32:
				realColor = 5;
				break;
			case 18:
			case 30:
				realColor = 2;
				break;
			case 19:
				realColor = 1;
				break;
			case 20:
			case 28:
			case 35:
				realColor = 4;
				break;
			case 21:
			case 22:
			case 29:
				realColor = 11;
				break;
			case 27:
			case 33:
				realColor = 7;
		}
		return (byte) ((realColor << 2) + (color & 0b11));
	}

	public static int convertTypeMeta2Legacy(int typeMeta) {
		int type = typeMeta >> 4;
		int meta = typeMeta & 15;
		type = convertType2Legacy(type);
		return (type << 4) | meta;
	}

	public static String stringToChat(String str) {
		return "\"" + str.replaceAll("\"","\\\\\"") + "\"";
	}

	public static String getUsernameOrElse(IEaglerXServerAPI<?> api, UUID uuid) {
		IBasePlayer<?> guh = api.getPlayerByUUID(uuid);
		if (guh == null) {
			return "" + uuid.hashCode();
		}
		return guh.getUsername();
	}
}
