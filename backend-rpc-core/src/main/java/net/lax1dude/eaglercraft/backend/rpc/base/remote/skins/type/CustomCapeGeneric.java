package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.SkinConverterExt;

public class CustomCapeGeneric extends BaseCustomCape {

	private final byte[] textureData;

	public CustomCapeGeneric(byte[] textureData) {
		this.textureData = textureData;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public boolean isCapeEnabled() {
		return true;
	}

	@Override
	public boolean isCapePreset() {
		return false;
	}

	@Override
	public int getPresetCapeId() {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a preset cape");
	}

	@Override
	public EnumPresetCapes getPresetCape() {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a preset cape");
	}

	@Override
	public boolean isCapeCustom() {
		return true;
	}

	@Override
	public void getCustomCapePixels_ABGR8_32x32(byte[] array, int offset) {
		SkinConverterExt.convertCape23x17RGBto32x32ABGR(textureData, 0, array, offset);
	}

	@Override
	public void getCustomCapePixels_eagler(byte[] array, int offset) {
		System.arraycopy(textureData, 0, array, offset, 1173);
	}

	@Override
	protected byte[] textureData() {
		return textureData;
	}

}
