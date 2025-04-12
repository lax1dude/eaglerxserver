package net.lax1dude.eaglercraft.backend.server.base.skins.type;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;

public class UnsafeUtil {

	public static byte[] unsafeGetPixelsDirect(IEaglerPlayerSkin skin) {
		return ((BaseCustomSkin)skin).textureDataV4();
	}

	public static byte[] unsafeGetPixelsDirect(IEaglerPlayerCape skin) {
		return ((BaseCustomCape)skin).textureData();
	}

}
