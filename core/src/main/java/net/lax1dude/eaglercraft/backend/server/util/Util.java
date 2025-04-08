package net.lax1dude.eaglercraft.backend.server.util;

import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import io.netty.buffer.ByteBuf;

public class Util {

	public static final byte[] ZERO_BYTES = new byte[0];

	public static byte[] newByteArray(int len) {
		return len > 0 ? new byte[len] : ZERO_BYTES;
	}

	public static long steadyTime() {
		return System.nanoTime() / 1000000l;
	}

	public static RuntimeException propagateReflectThrowable(Exception ex) {
		if(ex instanceof InvocationTargetException exx) {
			Throwable cause = exx.getCause();
			if(cause != null) {
				if(cause instanceof RuntimeException cause2) {
					return cause2;
				}
				return new RuntimeException("Encountered an InvocationTargetException while performing reflection", cause);
			}
		}else if(ex instanceof RuntimeException exx) {
			return exx;
		}
		return new RuntimeException("Could not perform reflection!", ex);
	}

	public static byte[] sha1(byte[] input) {
		try {
			return MessageDigest.getInstance("SHA-1").digest(input);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-1 is not supported on this JRE!", e);
		}
	}

	public static MessageDigest sha1() {
		try {
			return MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-1 is not supported on this JRE!", e);
		}
	}

	private static final String hex = "0123456789abcdef";

	public static String hash2string(byte[] b) {
		char[] ret = new char[b.length * 2];
		for (int i = 0; i < b.length; ++i) {
			int bb = (int) b[i] & 0xFF;
			ret[i * 2] = hex.charAt((bb >> 4) & 0xF);
			ret[i * 2 + 1] = hex.charAt(bb & 0xF);
		}
		return new String(ret);
	}

	public static String sanitizeJDBCURIForLogs(String uri) {
		//TODO
		return uri;
	}

	public static UUID createUUIDFromUndashed(String str) {
		if(str.length() != 32) {
			throw new IllegalArgumentException("Invalid UUID string length: " + str.length() + " != 32");
		}
		return new UUID(Long.parseUnsignedLong(str.substring(0, 16), 16), Long.parseUnsignedLong(str.substring(16), 16));
	}

	public static CharSequence toUUIDStringUndashed(UUID uuid) {
		String str = uuid.toString();
		StringBuilder builder = new StringBuilder(32);
		builder.append(str, 0, 8);
		builder.append(str, 9, 13);
		builder.append(str, 14, 18);
		builder.append(str, 19, 23);
		builder.append(str, 24, 36);
		return builder;
	}

	public static void dumpByteBuf(ByteBuf buf, int maxLen) {
		buf.markReaderIndex();
		try {
			StringBuilder builderA = new StringBuilder();
			StringBuilder builderB = new StringBuilder();
			int i = 0;
			while(i < maxLen && buf.isReadable()) {
				int val = buf.readUnsignedByte();
				builderA.append(hex.charAt(val >>> 4));
				builderA.append(hex.charAt(val & 0xF));
				builderA.append(' ');
				if(val == 0) {
					builderB.append("\\0 ");
				}else if(val == '\n') {
					builderB.append("\\n ");
				}else if(val == '\r') {
					builderB.append("\\r ");
				}else if(val == '\t') {
					builderB.append("\\t ");
				}else {
					builderB.append((char)val);
					builderB.append(' ');
				}
				if(++i % 8 == 0) {
					System.out.println(builderA + "  " + builderB);
					builderA = new StringBuilder();
					builderB = new StringBuilder();
				}
			}
			if(builderA.length() > 0) {
				System.out.println(builderA + "  " + builderB);
			}
		}finally {
			buf.resetReaderIndex();
		}
	}

}
