package net.lax1dude.eaglercraft.backend.server.api;

import javax.annotation.Nullable;

public class TLSManagerException extends Exception {

	private static final long serialVersionUID = -8110945929693568131L;

	public TLSManagerException() {
	}

	public TLSManagerException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public TLSManagerException(@Nullable String message) {
		super(message);
	}

	public TLSManagerException(@Nullable Throwable cause) {
		super(cause);
	}

}
