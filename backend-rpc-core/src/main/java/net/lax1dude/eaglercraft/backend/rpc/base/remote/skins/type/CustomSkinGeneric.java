package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.SkinConverterExt;

public class CustomSkinGeneric extends BaseCustomSkin implements IModelRewritable {

	private final int modelId;
	private byte[] textureDataV3;
	private byte[] textureDataV4;

	private CustomSkinGeneric(int modelId, byte[] textureDataV3, byte[] textureDataV4) {
		this.modelId = modelId;
		this.textureDataV3 = textureDataV3;
		this.textureDataV4 = textureDataV4;
	}

	public static CustomSkinGeneric createV3(int modelId, byte[] textureDataV3) {
		return new CustomSkinGeneric(modelId, textureDataV3, null);
	}

	public static CustomSkinGeneric createV4(int modelId, byte[] textureDataV4) {
		return new CustomSkinGeneric(modelId, null, textureDataV4);
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public boolean isSkinPreset() {
		return false;
	}

	@Override
	public int getPresetSkinId() {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a preset skin");
	}

	@Override
	public EnumPresetSkins getPresetSkin() {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a preset skin");
	}

	@Override
	public boolean isSkinCustom() {
		return true;
	}

	@Override
	public void getCustomSkinPixels_ABGR8_64x64(byte[] array, int offset) {
		System.arraycopy(textureDataV3(), 0, array, offset, 16384);
	}

	@Override
	public void getCustomSkinPixels_eagler(byte[] array, int offset) {
		System.arraycopy(textureDataV4(), 0, array, offset, 12288);
	}

	@Override
	public EnumSkinModel getCustomSkinModelId() {
		return EnumSkinModel.getById(modelId);
	}

	@Override
	protected int modelId() {
		return modelId;
	}

	@Override
	protected byte[] textureDataV3() {
		if(textureDataV3 != null) {
			return textureDataV3;
		}else {
			return textureDataV3 = SkinConverterExt.convertToV3Raw(textureDataV4);
		}
	}

	@Override
	protected byte[] textureDataV4() {
		if(textureDataV4 != null) {
			return textureDataV4;
		}else {
			return textureDataV4 = SkinConverterExt.convertToV4Raw(textureDataV3);
		}
	}

	@Override
	public IEaglerPlayerSkin rewriteModelInternal(int modelId) {
		if(modelId != this.modelId) {
			return new CustomSkinModelRw(this, modelId);
		}else {
			return this;
		}
	}

}
