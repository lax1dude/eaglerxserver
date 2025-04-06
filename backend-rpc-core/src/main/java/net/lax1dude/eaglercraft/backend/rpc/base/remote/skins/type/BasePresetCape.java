package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;

abstract class BasePresetCape implements IEaglerPlayerCape {

	protected abstract int presetId();

	public int hashCode() {
		return presetId();
	}

	public boolean equals(Object obj) {
		return this == obj || ((obj instanceof BasePresetCape) && ((BasePresetCape)this).presetId() == presetId());
	}

}
