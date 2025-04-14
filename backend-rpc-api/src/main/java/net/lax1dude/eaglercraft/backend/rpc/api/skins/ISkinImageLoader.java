package net.lax1dude.eaglercraft.backend.rpc.api.skins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public interface ISkinImageLoader {

	IEaglerPlayerSkin loadPresetSkin(int presetSkin);

	IEaglerPlayerSkin loadPresetSkin(EnumPresetSkins presetSkin);

	IEaglerPlayerSkin loadPresetSkin(UUID playerUUID);

	IEaglerPlayerCape loadPresetNoCape();

	IEaglerPlayerCape loadPresetCape(int presetCape);

	IEaglerPlayerCape loadPresetCape(EnumPresetCapes presetCape);

	IEaglerPlayerSkin loadSkinImageData_ARGB8I_64x64(int[] pixelsARGB8I, EnumSkinModel modelId);

	IEaglerPlayerSkin loadSkinImageData_ABGR8_64x64(byte[] pixelsABGR8, EnumSkinModel modelId);

	IEaglerPlayerSkin loadSkinImageData_eagler(byte[] pixelsEagler, EnumSkinModel modelId);

	IEaglerPlayerSkin loadSkinImageData_ARGB8I_64x32(int[] pixelsARGB8I, EnumSkinModel modelId);

	IEaglerPlayerSkin loadSkinImageData(BufferedImage image, EnumSkinModel modelId);

	IEaglerPlayerSkin loadSkinImageData(InputStream inputStream, EnumSkinModel modelId) throws IOException;

	IEaglerPlayerSkin loadSkinImageData(File imageFile, EnumSkinModel modelId) throws IOException;

	IEaglerPlayerSkin rewriteCustomSkinModelId(IEaglerPlayerSkin skin, EnumSkinModel modelId);

	IEaglerPlayerCape loadCapeImageData_ARGB8I_64x32(int[] pixelsARGB8I);

	IEaglerPlayerCape loadCapeImageData_ARGB8I_32x32(int[] pixelsARGB8I);

	IEaglerPlayerCape loadCapeImageData_ABGR8_32x32(byte[] pixelsABGR8);

	IEaglerPlayerCape loadCapeImageData_eagler(byte[] pixelsEagler);

	IEaglerPlayerCape loadCapeImageData(BufferedImage image);

	IEaglerPlayerCape loadCapeImageData(InputStream inputStream) throws IOException;

	IEaglerPlayerCape loadCapeImageData(File imageFile) throws IOException;

}
