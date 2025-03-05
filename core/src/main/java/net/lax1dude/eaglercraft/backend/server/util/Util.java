package net.lax1dude.eaglercraft.backend.server.util;

import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

	public static final byte[] ZERO_BYTES = new byte[0];

	public static byte[] newByteArray(int len) {
		return len > 0 ? new byte[len] : ZERO_BYTES;
	}

	public static long steadyTime() {
		return System.nanoTime() / 1000000l;
	}

	public static RuntimeException propagateReflectThrowable(Exception ex) {
		if(ex instanceof InvocationTargetException) {
			Throwable cause = ((InvocationTargetException)ex).getCause();
			if(cause != null) {
				if(cause instanceof RuntimeException) {
					return (RuntimeException) cause;
				}
				return new RuntimeException("Encountered an InvocationTargetException while performing reflection", cause);
			}
		}else if(ex instanceof RuntimeException) {
			return (RuntimeException) ex;
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

}
