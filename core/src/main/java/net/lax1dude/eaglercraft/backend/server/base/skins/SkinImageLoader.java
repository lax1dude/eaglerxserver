package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.api.skins.ISkinImageLoader;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.InternUtils;

public class SkinImageLoader {

	public static final ISkinImageLoader INSTANCE = new ISkinImageLoader() {
		@Override
		public IEaglerPlayerSkin loadSkinImageData64x64(int[] pixelsARGB8, EnumSkinModel modelId) {
			return SkinImageLoader.loadSkinImageData64x64(pixelsARGB8, modelId);
		}
		@Override
		public IEaglerPlayerSkin loadSkinImageData64x32(int[] pixelsARGB8, EnumSkinModel modelId) {
			return SkinImageLoader.loadSkinImageData64x32(pixelsARGB8, modelId);
		}
		@Override
		public IEaglerPlayerSkin loadSkinImageData(File imageFile, EnumSkinModel modelId) throws IOException {
			return SkinImageLoader.loadSkinImageData(imageFile, modelId);
		}
		@Override
		public IEaglerPlayerSkin loadSkinImageData(InputStream inputStream, EnumSkinModel modelId) throws IOException {
			return SkinImageLoader.loadSkinImageData(inputStream, modelId);
		}
		@Override
		public IEaglerPlayerSkin loadSkinImageData(BufferedImage image, EnumSkinModel modelId) {
			return SkinImageLoader.loadSkinImageData(image, modelId);
		}
		@Override
		public IEaglerPlayerSkin rewriteCustomSkinModelId(IEaglerPlayerSkin skin, EnumSkinModel modelId) {
			return SkinImageLoader.rewriteCustomSkinModelId(skin, modelId);
		}
		@Override
		public IEaglerPlayerSkin loadPresetSkin(UUID playerUUID) {
			return SkinImageLoader.loadPresetSkin(playerUUID);
		}
		@Override
		public IEaglerPlayerSkin loadPresetSkin(EnumPresetSkins presetSkin) {
			return SkinImageLoader.loadPresetSkin(presetSkin);
		}
		@Override
		public IEaglerPlayerSkin loadPresetSkin(int presetSkin) {
			return SkinImageLoader.loadPresetSkin(presetSkin);
		}
		@Override
		public IEaglerPlayerCape loadPresetNoCape() {
			return SkinImageLoader.loadPresetNoCape();
		}
		@Override
		public IEaglerPlayerCape loadPresetCape(EnumPresetCapes presetCape) {
			return SkinImageLoader.loadPresetCape(presetCape);
		}
		@Override
		public IEaglerPlayerCape loadPresetCape(int presetCape) {
			return SkinImageLoader.loadPresetCape(presetCape);
		}
		@Override
		public IEaglerPlayerCape loadCapeImageData64x32(int[] pixelsARGB8) {
			return SkinImageLoader.loadCapeImageData64x32(pixelsARGB8);
		}
		@Override
		public IEaglerPlayerCape loadCapeImageData32x32(int[] pixelsARGB8) {
			return SkinImageLoader.loadCapeImageData32x32(pixelsARGB8);
		}
		@Override
		public IEaglerPlayerCape loadCapeImageData(File imageFile) throws IOException {
			return SkinImageLoader.loadCapeImageData(imageFile);
		}
		@Override
		public IEaglerPlayerCape loadCapeImageData(InputStream inputStream) throws IOException {
			return SkinImageLoader.loadCapeImageData(inputStream);
		}
		@Override
		public IEaglerPlayerCape loadCapeImageData(BufferedImage image) {
			return SkinImageLoader.loadCapeImageData(image);
		}
	};

	public static IEaglerPlayerSkin loadPresetSkin(int presetSkin) {
		return InternUtils.getPresetSkin(presetSkin);
	}

	public static IEaglerPlayerSkin loadPresetSkin(EnumPresetSkins presetSkin) {
		return InternUtils.getPresetSkin(presetSkin.getId());
	}

	public static IEaglerPlayerSkin loadPresetSkin(UUID playerUUID) {
		return InternUtils.getPresetSkin((playerUUID.hashCode() & 1) != 0 ? 1 : 0);
	}

	public static IEaglerPlayerSkin rewriteCustomSkinModelId(IEaglerPlayerSkin skin, EnumSkinModel modelId) {
		return null;
	}

	public static IEaglerPlayerCape loadPresetNoCape() {
		return InternUtils.getPresetCape(0);
	}

	public static IEaglerPlayerCape loadPresetCape(int presetCape) {
		return InternUtils.getPresetCape(presetCape);
	}

	public static IEaglerPlayerCape loadPresetCape(EnumPresetCapes presetCape) {
		return InternUtils.getPresetCape(presetCape.getId());
	}

	public static IEaglerPlayerSkin loadSkinImageData64x64(int[] pixelsARGB8, EnumSkinModel modelId) {
		return null;
	}

	public static IEaglerPlayerSkin loadSkinImageData64x32(int[] pixelsARGB8, EnumSkinModel modelId) {
		return null;
	}

	public static IEaglerPlayerSkin loadSkinImageData(BufferedImage image, EnumSkinModel modelId) {
		return null;
	}

	public static IEaglerPlayerSkin loadSkinImageData(InputStream inputStream, EnumSkinModel modelId) throws IOException {
		return null;
	}

	public static IEaglerPlayerSkin loadSkinImageData(File imageFile, EnumSkinModel modelId) throws IOException {
		return null;
	}

	public static IEaglerPlayerCape loadCapeImageData64x32(int[] pixelsARGB8) {
		return null;
	}

	public static IEaglerPlayerCape loadCapeImageData32x32(int[] pixelsARGB8) {
		return null;
	}

	public static IEaglerPlayerCape loadCapeImageData(BufferedImage image) {
		return null;
	}

	public static IEaglerPlayerCape loadCapeImageData(InputStream inputStream) throws IOException {
		return null;
	}

	public static IEaglerPlayerCape loadCapeImageData(File imageFile) throws IOException {
		return null;
	}

}
