package net.lax1dude.eaglercraft.backend.server.adapter;

public class AbortLoadException extends RuntimeException {

	public AbortLoadException(String message) {
		super(message);
	}

	public AbortLoadException(String message, Throwable reason) {
		super(message, reason);
	}

}
