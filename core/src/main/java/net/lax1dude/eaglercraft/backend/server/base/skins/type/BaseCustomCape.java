package net.lax1dude.eaglercraft.backend.server.base.skins.type;

import java.util.Arrays;

abstract class BaseCustomCape {

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
