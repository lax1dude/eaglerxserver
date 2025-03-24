package net.lax1dude.eaglercraft.backend.rpc.api;

public enum EnumPlatformType {
	BUKKIT("Bukkit");

	private final String name;

	private EnumPlatformType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
