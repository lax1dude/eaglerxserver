package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type;

import java.util.Arrays;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;

abstract class BaseCustomSkin implements IEaglerPlayerSkin {

	private int hashCode;
	private boolean hashZero;

	protected abstract int modelId();

	protected abstract byte[] textureDataV3();

	protected abstract byte[] textureDataV4();

	public int hashCode() {
		if(hashCode == 0 && !hashZero) {
			hashCode = Arrays.hashCode(textureDataV4());
			if(hashCode == 0) {
				hashZero = true;
			}
		}
		return hashCode;
	}

	public boolean equals(Object obj) {
		return this == obj || ((obj instanceof BaseCustomSkin cc) && (hashCode() == cc.hashCode())
				&& modelId() == cc.modelId() && Arrays.equals(textureDataV4(), cc.textureDataV4()));
	}

}
