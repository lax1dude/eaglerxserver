package net.lax1dude.eaglercraft.backend.server.api;

import javax.annotation.Nullable;

public enum EnumCapabilityType {
	UPDATE(0),
	VOICE(1),
	REDIRECT(2),
	NOTIFICATION(3),
	PAUSE_MENU(4),
	WEBVIEW(5),
	COOKIE(6),
	EAGLER_IP(7);

	private final int id;
	private final int bit;

	private EnumCapabilityType(int id) {
		this.id = id;
		this.bit = 1 << id;
	}

	public int getId() {
		return id;
	}

	public int getBit() {
		return bit;
	}

	@Nullable
	public static EnumCapabilityType getById(int id) {
		return id >= 0 && id < LOOKUP.length ? LOOKUP[id] : null;
	}

	private static final EnumCapabilityType[] LOOKUP = new EnumCapabilityType[8];

	static {
		for(EnumCapabilityType cap : values()) {
			LOOKUP[cap.id] = cap;
		}
	}

}
