package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import io.netty.buffer.ByteBuf;

public class BufferUtils {

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

	public static void writeVarInt(ByteBuf buffer, int val) {
		int part;
		while (true) {
			part = val & 0x7F;

			val >>>= 7;
			if (val != 0) {
				part |= 0x80;
			}

			buffer.writeByte(part);

			if (val == 0) {
				break;
			}
		}
	}

	public static int varIntLength(int val) {
		for (int i = 1; i < 5; ++i) {
			if ((val & -1 << i * 7) == 0) {
				return i;
			}
		}

		return 5;
	}

}
