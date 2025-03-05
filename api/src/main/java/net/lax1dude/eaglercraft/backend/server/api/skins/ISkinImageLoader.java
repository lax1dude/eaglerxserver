package net.lax1dude.eaglercraft.backend.server.api.skins;

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

	IEaglerPlayerSkin loadSkinImageData64x64(int[] pixelsARGB8, EnumSkinModel modelId);

	IEaglerPlayerSkin loadSkinImageData64x32(int[] pixelsARGB8, EnumSkinModel modelId);

	IEaglerPlayerSkin loadSkinImageData(BufferedImage image, EnumSkinModel modelId);

	IEaglerPlayerSkin loadSkinImageData(InputStream inputStream, EnumSkinModel modelId) throws IOException;

	IEaglerPlayerSkin loadSkinImageData(File imageFile, EnumSkinModel modelId) throws IOException;

	IEaglerPlayerSkin rewriteCustomSkinModelId(IEaglerPlayerSkin skin, EnumSkinModel modelId);

	IEaglerPlayerCape loadCapeImageData64x32(int[] pixelsARGB8);

	IEaglerPlayerCape loadCapeImageData32x32(int[] pixelsARGB8);

	IEaglerPlayerCape loadCapeImageData(BufferedImage image);

	IEaglerPlayerCape loadCapeImageData(InputStream inputStream) throws IOException;

	IEaglerPlayerCape loadCapeImageData(File imageFile) throws IOException;

}
