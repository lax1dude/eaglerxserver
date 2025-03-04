package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

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

	public static ByteBuf convertSlot2Legacy(ByteBuf buffer, ByteBufAllocator alloc) {
		ByteBuf bb = alloc.buffer();
		short blockId = buffer.readShort();
		bb.writeShort(blockId);
		if (blockId == -1) {
			return bb;
		}
		byte itemCount = buffer.readByte();
		short itemDamage = buffer.readShort();
		bb.writeByte(itemCount);
		bb.writeShort(itemDamage);
		byte test = buffer.getByte(0);
		if (test == 0x00) {
			bb.writeShort(-1);
			return bb;
		}
		ByteBuf nbt = convertNBT2Legacy(buffer, alloc);
		bb.writeShort(nbt.readableBytes());
		// todo: gzip it, with rfc 1952
		bb.writeBytes(nbt);
		return bb;
	}

	public static ByteBuf convertLegacySlot(ByteBuf buffer, ByteBufAllocator alloc) {
		ByteBuf bb = alloc.buffer();
		short blockId = buffer.readShort();
		bb.writeShort(blockId);
		if (blockId == -1) {
			return bb;
		}
		byte itemCount = buffer.readByte();
		short itemDamage = buffer.readShort();
		bb.writeByte(itemCount);
		bb.writeShort(itemDamage);
		short len = buffer.readShort();
		if (len == -1) {
			bb.writeByte(0);
			return bb;
		}
		// todo: ungzip buffer using len for length to ungzip!! rfc 1952
		ByteBuf nbt = convertLegacyNBT(buffer, alloc);
		bb.writeBytes(nbt);
		return bb;
	}

	public static ByteBuf convertNBT2Legacy(ByteBuf buffer, ByteBufAllocator alloc) {
		return buffer;
	}

	public static ByteBuf convertLegacyNBT(ByteBuf buffer, ByteBufAllocator alloc) {
		return buffer;
	}

	// "also use an Inflater and Deflater not an InflaterInputStream/DeflaterOutputStream"

	public static ByteBuf convertChunk2Legacy(boolean skyLightSent, int pbm, boolean guc, ByteBuf buffer, ByteBufAllocator alloc) {
		ByteBuf bb = alloc.buffer();

		if (buffer.readUnsignedByte() > 0) {
			// oh no
			int len = BufferUtils.readVarInt(buffer);
			for (int i = 0; i < len; ++i) {
				BufferUtils.readVarInt(buffer);
			}
			// assume ALWAYS global palette (PLEASE)
		}

		for (int i = 0; i < 16; ++i) {
			if ((pbm & (1 << i)) != 0) {
				byte[] outputBlockMetadataArray = new byte[2048];

				int dataArrayLength = BufferUtils.readVarInt(buffer);
				for (int ii = 0; ii < dataArrayLength; ++ii) {
					long data = buffer.readLong();
					byte metadata = (byte) (data & 0xF);
					byte type = (byte) (data >> 4);
					bb.writeByte(type);
					if (ii % 2 == 0) {
						outputBlockMetadataArray[ii / 2] = (byte) (metadata << 4);
					} else {
						outputBlockMetadataArray[ii / 2] |= (byte) (metadata & 0xF);
					}
				}
				bb.writeBytes(outputBlockMetadataArray);
				bb.writeBytes(buffer, skyLightSent ? 4096 : 2048);
			}
		}
		if (guc) {
			bb.writeBytes(buffer, 256);
		}

		// todo: compress zlib deflate
		return bb;
	}
}
