package net.lax1dude.eaglercraft.backend.rpc.api;

import javax.annotation.Nonnull;

public enum EnumPlatformType {
	BUKKIT("Bukkit");

	private final String name;

	private EnumPlatformType(String name) {
		this.name = name;
	}

	@Nonnull
	public String getName() {
		return name;
	}

}
