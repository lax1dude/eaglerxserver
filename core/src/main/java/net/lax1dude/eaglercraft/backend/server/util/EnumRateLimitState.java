package net.lax1dude.eaglercraft.backend.server.util;

public enum EnumRateLimitState {
	OK(false, false), BLOCKED(true, false),
	BLOCKED_LOCKED(false, true), LOCKED(false, true);

	private final boolean ok;
	private final boolean blocked;
	private final boolean locked;

	private EnumRateLimitState(boolean blocked, boolean locked) {
		this.ok = !blocked && !locked;
		this.blocked = blocked;
		this.locked = locked;
	}

	public boolean isOk() {
		return ok;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public boolean isLocked() {
		return locked;
	}

}