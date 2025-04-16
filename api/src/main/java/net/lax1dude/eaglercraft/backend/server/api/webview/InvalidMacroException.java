package net.lax1dude.eaglercraft.backend.server.api.webview;

import javax.annotation.Nullable;

public class InvalidMacroException extends RuntimeException {

	private static final long serialVersionUID = -4570840949828814745L;

	public InvalidMacroException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public InvalidMacroException(@Nullable String message) {
		super(message);
	}

}