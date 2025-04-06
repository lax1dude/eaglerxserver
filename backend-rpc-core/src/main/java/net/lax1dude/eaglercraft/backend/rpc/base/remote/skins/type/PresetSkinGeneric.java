package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumSkinModel;

public class PresetSkinGeneric extends BasePresetSkin {

	private final int presetId;

	PresetSkinGeneric(int presetId) {
		this.presetId = presetId;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public boolean isSkinPreset() {
		return true;
	}

	@Override
	public int getPresetSkinId() {
		return presetId;
	}

	@Override
	public EnumPresetSkins getPresetSkin() {
		return EnumPresetSkins.getByIdOrDefault(presetId);
	}

	@Override
	public boolean isSkinCustom() {
		return false;
	}

	@Override
	public void getCustomSkinPixels_RGBA8_64x64(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
	}

	@Override
	public void getCustomSkinPixels_eagler(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
	}

	@Override
	public EnumSkinModel getCustomSkinModelId() {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
	}

	@Override
	protected int presetId() {
		return presetId;
	}

}
