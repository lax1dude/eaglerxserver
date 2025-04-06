package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type;

import java.util.Arrays;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;

abstract class BaseCustomCape implements IEaglerPlayerCape {

	private int hashCode;
	private boolean hashZero;

	protected abstract byte[] textureData();

	public int hashCode() {
		if(hashCode == 0 && !hashZero) {
			hashCode = Arrays.hashCode(textureData());
			if(hashCode == 0) {
				hashZero = true;
			}
		}
		return hashCode;
	}

	public boolean equals(Object obj) {
		return this == obj || ((obj instanceof BaseCustomCape) && (hashCode() == obj.hashCode())
				&& Arrays.equals(((BaseCustomCape) obj).textureData(), textureData()));
	}

}
