package net.lax1dude.eaglercraft.backend.server.api.webview;

public class InvalidMacroException extends RuntimeException {

	private static final long serialVersionUID = -4570840949828814745L;

	public InvalidMacroException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidMacroException(String message) {
		super(message);
	}

}