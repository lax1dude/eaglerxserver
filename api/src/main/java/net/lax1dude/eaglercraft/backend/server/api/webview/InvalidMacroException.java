package net.lax1dude.eaglercraft.backend.server.api.webview;

public class InvalidMacroException extends RuntimeException {

	public InvalidMacroException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidMacroException(String message) {
		super(message);
	}

}