package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.annotation.Nonnull;

public interface ISkinImageLoader {

	@Nonnull
	IEaglerPlayerSkin loadPresetSkin(int presetSkin);

	@Nonnull
	IEaglerPlayerSkin loadPresetSkin(@Nonnull EnumPresetSkins presetSkin);

	@Nonnull
	IEaglerPlayerSkin loadPresetSkin(@Nonnull UUID playerUUID);

	@Nonnull
	IEaglerPlayerCape loadPresetNoCape();

	@Nonnull
	IEaglerPlayerCape loadPresetCape(int presetCape);

	@Nonnull
	IEaglerPlayerCape loadPresetCape(@Nonnull EnumPresetCapes presetCape);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData_ARGB8I_64x64(@Nonnull int[] pixelsARGB8I, @Nonnull EnumSkinModel modelId);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData_ABGR8_64x64(@Nonnull byte[] pixelsABGR8, @Nonnull EnumSkinModel modelId);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData_eagler(@Nonnull byte[] pixelsEagler, @Nonnull EnumSkinModel modelId);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData_ARGB8I_64x32(@Nonnull int[] pixelsARGB8I, @Nonnull EnumSkinModel modelId);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData(@Nonnull BufferedImage image, @Nonnull EnumSkinModel modelId);

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData(@Nonnull InputStream inputStream, @Nonnull EnumSkinModel modelId) throws IOException;

	@Nonnull
	IEaglerPlayerSkin loadSkinImageData(@Nonnull File imageFile, @Nonnull EnumSkinModel modelId) throws IOException;

	@Nonnull
	IEaglerPlayerSkin rewriteCustomSkinModelId(@Nonnull IEaglerPlayerSkin skin, @Nonnull EnumSkinModel modelId);

	@Nonnull
	IEaglerPlayerCape loadCapeImageData_ARGB8I_64x32(@Nonnull int[] pixelsARGB8I);

	@Nonnull
	IEaglerPlayerCape loadCapeImageData_ARGB8I_32x32(@Nonnull int[] pixelsARGB8I);

	@Nonnull
	IEaglerPlayerCape loadCapeImageData_ABGR8_32x32(@Nonnull byte[] pixelsABGR8);

	@Nonnull
	IEaglerPlayerCape loadCapeImageData_eagler(@Nonnull byte[] pixelsEagler);

	@Nonnull
	IEaglerPlayerCape loadCapeImageData(@Nonnull BufferedImage image);

	@Nonnull
	IEaglerPlayerCape loadCapeImageData(@Nonnull InputStream inputStream) throws IOException;

	@Nonnull
	IEaglerPlayerCape loadCapeImageData(@Nonnull File imageFile) throws IOException;

}
