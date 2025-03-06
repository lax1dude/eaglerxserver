package net.lax1dude.eaglercraft.backend.server.base.skins.type;

import java.util.Arrays;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;

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
		if(this == obj) {
			return true;
		}else {
			if((obj instanceof BaseCustomSkin) && (hashCode() == obj.hashCode())) {
				BaseCustomSkin cc = (BaseCustomSkin) obj;
				return cc.modelId() == modelId() && Arrays.equals(cc.textureDataV4(), textureDataV4());
			}else {
				return false;
			}
		}
	}

}
