package net.lax1dude.eaglercraft.backend.server.util;

import java.lang.reflect.InvocationTargetException;

public class Util {

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
		}
		return new RuntimeException("Could not perform reflection!", ex);
	}

}
