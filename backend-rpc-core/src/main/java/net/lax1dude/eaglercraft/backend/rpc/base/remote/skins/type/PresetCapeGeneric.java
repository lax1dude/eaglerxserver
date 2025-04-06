package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;

public class PresetCapeGeneric extends BasePresetCape {

	private final int presetId;

	PresetCapeGeneric(int presetId) {
		this.presetId = presetId;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public boolean isCapeEnabled() {
		return presetId != 0;
	}

	@Override
	public boolean isCapePreset() {
		return true;
	}

	@Override
	public int getPresetCapeId() {
		return presetId;
	}

	@Override
	public EnumPresetCapes getPresetCape() {
		return EnumPresetCapes.getByIdOrDefault(presetId);
	}

	@Override
	public boolean isCapeCustom() {
		return false;
	}

	@Override
	public void getCustomCapePixels_RGBA8_32x32(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a custom cape");
	}

	@Override
	public void getCustomCapePixels_eagler(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a custom cape");
	}

	@Override
	protected int presetId() {
		return presetId;
	}

}
