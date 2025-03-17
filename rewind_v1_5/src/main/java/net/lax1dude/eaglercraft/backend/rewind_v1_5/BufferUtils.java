package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import io.netty.buffer.ByteBuf;

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
			throw new IndexOutOfBoundsException();
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
		// todo: convert 1.8 items to 1.5
		short blockId = buffer.readShort();
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
		// todo: convert 1.5 items to 1.8
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
		if (buffer.readByte() == 0) {
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

	public static void convertChunk2Legacy(boolean skyLightSent, int sbm, boolean guc, ByteBuf buffer, ByteBuf bb) {
		for (int i = 0; i < 16; ++i) {
			if ((sbm & (1 << i)) != 0) {
				int ind1 = bb.writerIndex();

				for (int ii = 0; ii < 2048; ++ii) {
					short data1 = buffer.readShort();
					data1 = (short) BufferUtils.convertTypeMeta2Legacy(data1);
					byte type1 = (byte) (data1 >> 4);
					byte metadata1 = (byte) (data1 & 15);
					short data2 = buffer.readShort();
					data2 = (short) BufferUtils.convertTypeMeta2Legacy(data2);
					byte type2 = (byte) (data2 >> 4);
					byte metadata2 = (byte) (data2 & 15);
					bb.writeByte(type1);
					bb.writeByte(type2);
					bb.setByte(ind1 + 4096 + (ii / 2), (metadata1 << 4) | (metadata2 & 15));
				}
				bb.writerIndex(ind1 + 6144);
				bb.writeBytes(buffer, skyLightSent ? 4096 : 2048);
			}
		}
		if (guc) {
			bb.writeBytes(buffer, 256);
		}
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

	public static void convertMetadata2Legacy(ByteBuf buffer, ByteBuf bb) {
		boolean allDumb = true;
		while (true) {
			short item = buffer.readUnsignedByte();
			if (item == 127) {
				break;
			}
			int type = (item & 224) >> 5;
			if (type == 7) {
				buffer.readFloat();
				buffer.readFloat();
				buffer.readFloat();
				continue;
			}
			allDumb = false;
			bb.writeByte(item);
			switch (type) {
				case 0:
					bb.writeByte(buffer.readByte());
					break;
				case 1:
					bb.writeShort(buffer.readShort());
					break;
				case 2:
					bb.writeInt(buffer.readInt());
					break;
				case 3:
					bb.writeFloat(buffer.readFloat());
					break;
				case 4:
					BufferUtils.writeLegacyMCString(bb, BufferUtils.readMCString(buffer, 32767), 32767);
					break;
				case 5:
					BufferUtils.convertSlot2Legacy(buffer, bb);
					break;
				case 6:
					bb.writeInt(buffer.readInt());
					bb.writeInt(buffer.readInt());
					bb.writeInt(buffer.readInt());
					break;
			}
		}
		if (allDumb) {
			bb.writeByte(0xFF);
		}
	}

	public static int convertType2Legacy(int type) {
		// todo: map type
		// return type;
		return 1;
	}

	public static int convertTypeMeta2Legacy(int typeMeta) {
		int type = typeMeta >> 4;
		int meta = typeMeta & 15;
		type = convertType2Legacy(type);
		// todo: map meta
		meta = 0;
		return (type << 4) | meta;
	}

	public static String stringToChat(String str) {
		return "\"" + str.replaceAll("\"","\\\\\"") + "\"";
	}
}
