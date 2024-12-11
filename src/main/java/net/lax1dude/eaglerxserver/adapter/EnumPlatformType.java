package net.lax1dude.eaglerxserver.adapter;

public enum EnumPlatformType {
	BUNGEE(true),
	VELOCITY(true),
	BUKKIT(false);

	public final boolean proxy;

	private EnumPlatformType(boolean proxy) {
		this.proxy = proxy;
	}

}
