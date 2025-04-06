package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;

public class MissingCape extends BasePresetCape {

	public static final IEaglerPlayerCape MISSING_CAPE = new MissingCape();

	private MissingCape() {
	}

	@Override
	public boolean isSuccess() {
		return false;
	}

	@Override
	public boolean isCapeEnabled() {
		return false;
	}

	@Override
	public boolean isCapePreset() {
		return true;
	}

	@Override
	public int getPresetCapeId() {
		return 0;
	}

	@Override
	public EnumPresetCapes getPresetCape() {
		return EnumPresetCapes.NO_CAPE;
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
		return 0;
	}

}
