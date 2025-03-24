package net.lax1dude.eaglercraft.backend.rpc.api.skins;

import net.lax1dude.eaglercraft.backend.rpc.api.IOptional;

public interface IEaglerPlayerSkin extends IOptional<IEaglerPlayerSkin> {

	boolean isSkinPreset();

	int getPresetSkinId();

	EnumPresetSkins getPresetSkin();

	boolean isSkinCustom();

	default void getCustomSkinPixels_RGBA8_64x64(byte[] array) {
		getCustomSkinPixels_RGBA8_64x64(array, 0);
	}

	void getCustomSkinPixels_RGBA8_64x64(byte[] array, int offset);

	default void getCustomSkinPixels_eagler(byte[] array) {
		getCustomSkinPixels_eagler(array, 0);
	}

	void getCustomSkinPixels_eagler(byte[] array, int offset);

	EnumSkinModel getCustomSkinModelId();

}
