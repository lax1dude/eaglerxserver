package net.lax1dude.eaglercraft.backend.rpc.api.skins;

import net.lax1dude.eaglercraft.backend.rpc.api.IOptional;

public interface IEaglerPlayerSkin extends IOptional<IEaglerPlayerSkin> {

	boolean isSkinPreset();

	int getPresetSkinId();

	EnumPresetSkins getPresetSkin();

	boolean isSkinCustom();

	default byte[] getCustomSkinPixels_ABGR8_64x64() {
		byte[] array = new byte[16384];
		getCustomSkinPixels_ABGR8_64x64(array, 0);
		return array;
	}

	default void getCustomSkinPixels_ABGR8_64x64(byte[] array) {
		getCustomSkinPixels_ABGR8_64x64(array, 0);
	}

	void getCustomSkinPixels_ABGR8_64x64(byte[] array, int offset);

	default byte[] getCustomSkinPixels_eagler() {
		byte[] array = new byte[12288];
		getCustomSkinPixels_eagler(array, 0);
		return array;
	}

	default void getCustomSkinPixels_eagler(byte[] array) {
		getCustomSkinPixels_eagler(array, 0);
	}

	void getCustomSkinPixels_eagler(byte[] array, int offset);

	EnumSkinModel getCustomSkinModelId();

}
