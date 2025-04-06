package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.ISkinImageLoader;

class SkinImageLoaderCacheOff implements ISkinImageLoader {

	static final ISkinImageLoader INSTANCE = new SkinImageLoaderCacheOff();

	SkinImageLoaderCacheOff() {
	}

	@Override
	public IEaglerPlayerSkin loadPresetSkin(int presetSkin) {
		return SkinImageLoaderImpl.loadPresetSkin(presetSkin);
	}

	@Override
	public IEaglerPlayerSkin loadPresetSkin(EnumPresetSkins presetSkin) {
		return SkinImageLoaderImpl.loadPresetSkin(presetSkin);
	}

	@Override
	public IEaglerPlayerSkin loadPresetSkin(UUID playerUUID) {
		return SkinImageLoaderImpl.loadPresetSkin(playerUUID);
	}

	@Override
	public IEaglerPlayerCape loadPresetNoCape() {
		return SkinImageLoaderImpl.loadPresetNoCape();
	}

	@Override
	public IEaglerPlayerCape loadPresetCape(int presetCape) {
		return SkinImageLoaderImpl.loadPresetCape(presetCape);
	}

	@Override
	public IEaglerPlayerCape loadPresetCape(EnumPresetCapes presetCape) {
		return SkinImageLoaderImpl.loadPresetCape(presetCape);
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData64x64(int[] pixelsARGB8, EnumSkinModel modelId) {
		return SkinImageLoaderImpl.loadSkinImageData64x64(pixelsARGB8, modelId);
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData64x64(byte[] pixelsRGBA8, EnumSkinModel modelId) {
		return SkinImageLoaderImpl.loadSkinImageData64x64(pixelsRGBA8, modelId);
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData64x64Eagler(byte[] pixelsEagler, EnumSkinModel modelId) {
		return SkinImageLoaderImpl.loadSkinImageData64x64Eagler(pixelsEagler, modelId);
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData64x32(int[] pixelsARGB8, EnumSkinModel modelId) {
		return SkinImageLoaderImpl.loadSkinImageData64x32(pixelsARGB8, modelId);
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData(BufferedImage image, EnumSkinModel modelId) {
		return SkinImageLoaderImpl.loadSkinImageData(image, modelId);
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData(InputStream inputStream, EnumSkinModel modelId) throws IOException {
		return SkinImageLoaderImpl.loadSkinImageData(inputStream, modelId);
	}

	@Override
	public IEaglerPlayerSkin loadSkinImageData(File imageFile, EnumSkinModel modelId) throws IOException {
		return SkinImageLoaderImpl.loadSkinImageData(imageFile, modelId);
	}

	@Override
	public IEaglerPlayerSkin rewriteCustomSkinModelId(IEaglerPlayerSkin skin, EnumSkinModel modelId) {
		return SkinImageLoaderImpl.rewriteCustomSkinModelId(skin, modelId);
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData64x32(int[] pixelsARGB8) {
		return SkinImageLoaderImpl.loadCapeImageData64x32(pixelsARGB8);
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData32x32(int[] pixelsARGB8) {
		return SkinImageLoaderImpl.loadCapeImageData32x32(pixelsARGB8);
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData32x32(byte[] pixelsRGBA8) {
		return SkinImageLoaderImpl.loadCapeImageData32x32(pixelsRGBA8);
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData23x17Eagler(byte[] pixelsEagler) {
		return SkinImageLoaderImpl.loadCapeImageData23x17Eagler(pixelsEagler);
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData(BufferedImage image) {
		return SkinImageLoaderImpl.loadCapeImageData(image);
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData(InputStream inputStream) throws IOException {
		return SkinImageLoaderImpl.loadCapeImageData(inputStream);
	}

	@Override
	public IEaglerPlayerCape loadCapeImageData(File imageFile) throws IOException {
		return SkinImageLoaderImpl.loadCapeImageData(imageFile);
	}

}
