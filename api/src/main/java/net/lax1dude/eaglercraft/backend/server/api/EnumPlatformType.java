package net.lax1dude.eaglercraft.backend.server.api;

import javax.annotation.Nonnull;

public enum EnumPlatformType {
	BUNGEECORD("BungeeCord", true, false),
	VELOCITY("Velocity", true, false),
	BUKKIT("Bukkit", false, false),
	STANDALONE("Standalone", true, true);

	private final String name;
	private final boolean proxy;
	private final boolean standalone;

	private EnumPlatformType(String name, boolean proxy, boolean standalone) {
		this.name = name;
		this.proxy = proxy;
		this.standalone = standalone;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public boolean isProxy() {
		return proxy;
	}

	public boolean isStandalone() {
		return standalone;
	}

}
