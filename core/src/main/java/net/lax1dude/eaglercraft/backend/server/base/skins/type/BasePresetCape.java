package net.lax1dude.eaglercraft.backend.server.base.skins.type;

abstract class BasePresetCape {

	protected abstract int presetId();

	public int hashCode() {
		return presetId();
	}

	public boolean equals(Object obj) {
		return this == obj || ((obj instanceof BasePresetCape) && ((BasePresetCape)this).presetId() == presetId());
	}

}
