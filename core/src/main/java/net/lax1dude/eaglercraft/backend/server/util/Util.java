package net.lax1dude.eaglercraft.backend.server.util;

import java.lang.reflect.InvocationTargetException;

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

}
