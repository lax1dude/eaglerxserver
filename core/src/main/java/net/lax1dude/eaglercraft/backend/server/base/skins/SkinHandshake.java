package net.lax1dude.eaglercraft.backend.server.base.skins;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinImageLoader;

public class SkinHandshake {

	public static final int PACKET_MY_SKIN_PRESET = 0x01;
	public static final int PACKET_MY_SKIN_CUSTOM = 0x02;
	public static final int PACKET_MY_CAPE_PRESET = 0x01;
	public static final int PACKET_MY_CAPE_CUSTOM = 0x02;

	public static IEaglerPlayerSkin loadSkinDataV1(ISkinImageLoader factory, byte[] data) {
		return null;//TODO
	}

	public static IEaglerPlayerSkin loadSkinDataV2(ISkinImageLoader factory, byte[] data) {
		return null;//TODO
	}

	public static IEaglerPlayerCape loadCapeDataV1(ISkinImageLoader factory, byte[] data) {
		return null;//TODO
	}

}
