package net.lax1dude.eaglercraft.backend.server.base.pipeline;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;

public class BufferUtils {

	public static final boolean CHARSEQ_SUPPORT;

	static {
		boolean b = false;
		try {
			ByteBuf.class.getMethod("readCharSequence", int.class, Charset.class);
			b = true;
		}catch(ReflectiveOperationException ex) {
		}
		CHARSEQ_SUPPORT = b;
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

	public static CharSequence readCharSequence(ByteBuf buffer, int len, Charset charset) {
		if(CHARSEQ_SUPPORT) {
			return buffer.readCharSequence(len, charset);
		}else {
			byte[] buf = new byte[len];
			buffer.readBytes(buf);
			return new String(buf, charset);
		}
	}

	public static int writeCharSequence(ByteBuf buffer, CharSequence seq, Charset charset) {
		if(CHARSEQ_SUPPORT) {
			return buffer.writeCharSequence(seq, charset);
		}else {
			byte[] bytes = seq.toString().getBytes(charset);
			buffer.writeBytes(bytes);
			return bytes.length;
		}
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
		int len = BufferUtils.readVarInt(buffer, 5);
		if(len > maxLen * 4) {
			throw new IndexOutOfBoundsException();
		}
		CharSequence ret = readCharSequence(buffer, len, StandardCharsets.UTF_8);
		if(ret.length() > maxLen) {
			throw new IndexOutOfBoundsException();
		}
		return ret.toString();
	}

	public static CharSequence readMCCharSequence(ByteBuf buffer, int maxLen) {
		int len = BufferUtils.readVarInt(buffer, 5);
		if(len > maxLen * 4) {
			throw new IndexOutOfBoundsException();
		}
		CharSequence ret = readCharSequence(buffer, len, StandardCharsets.UTF_8);
		if(ret.length() > maxLen) {
			throw new IndexOutOfBoundsException();
		}
		return ret;
	}

	public static void writeMCString(ByteBuf buffer, String value, int maxLen) {
		if(value.length() > maxLen) {
			throw new IndexOutOfBoundsException();
		}
		byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		BufferUtils.writeVarInt(buffer, bytes.length);
		buffer.writeBytes(bytes);
	}

	public static boolean charSeqEqual(CharSequence seq1, CharSequence seq2) {
		int l = seq1.length();
		if(l != seq2.length()) {
			return false;
		}
		for(int i = 0; i < l; ++i) {
			if(seq1.charAt(i) != seq2.charAt(i)) {
				return false;
			}
		}
		return true;
	}

}
