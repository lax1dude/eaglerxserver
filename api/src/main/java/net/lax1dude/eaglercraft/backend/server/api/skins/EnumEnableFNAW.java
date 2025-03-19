package net.lax1dude.eaglercraft.backend.server.api.skins;

public enum EnumEnableFNAW {
	ENABLED(true), DISABLED(false), FORCED(true);

	private final boolean enable;

	private EnumEnableFNAW(boolean en) {
		this.enable = en;
	}

	public boolean isEnabled() {
		return enable;
	}

	public boolean isForced() {
		return this == FORCED;
	}

}
