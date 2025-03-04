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
		return null;
	}

	public static IEaglerPlayerSkin loadPresetSkin(EnumPresetSkins presetSkin) {
		return null;
	}

	public static IEaglerPlayerSkin loadPresetSkin(UUID playerUUID) {
		return null;
	}

	public static IEaglerPlayerCape loadPresetNoCape() {
		return null;
	}

	public static IEaglerPlayerCape loadPresetCape(int presetCape) {
		return null;
	}

	public static IEaglerPlayerCape loadPresetCape(EnumPresetCapes presetCape) {
		return null;
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
