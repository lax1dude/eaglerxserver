package net.lax1dude.eaglercraft.backend.server.base.skins.type;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;

abstract class BasePresetSkin implements IEaglerPlayerSkin {

	protected abstract int presetId();

	public int hashCode() {
		return presetId();
	}

	public boolean equals(Object obj) {
		return this == obj || ((obj instanceof BasePresetSkin) && ((BasePresetSkin)this).presetId() == presetId());
	}

}
