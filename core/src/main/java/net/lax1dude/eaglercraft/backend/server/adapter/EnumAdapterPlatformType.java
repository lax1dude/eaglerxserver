package net.lax1dude.eaglercraft.backend.server.adapter;

public enum EnumAdapterPlatformType {
	BUNGEE(true),
	VELOCITY(true),
	BUKKIT(false);

	public final boolean proxy;

	private EnumAdapterPlatformType(boolean proxy) {
		this.proxy = proxy;
	}

}
