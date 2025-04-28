/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.rpc.base.remote.skins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;

import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.rpc.api.skins.ISkinImageLoader;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type.CustomCapeGeneric;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type.CustomSkinGeneric;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type.IModelRewritable;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.skins.type.InternUtils;

public class SkinImageLoaderImpl {

	public static ISkinImageLoader getSkinLoader(boolean cacheEnabled) {
		return cacheEnabled ? SkinImageLoaderCacheOn.INSTANCE : SkinImageLoaderCacheOff.INSTANCE;
	}

	public static IEaglerPlayerSkin loadPresetSkin(int presetSkin) {
		if(presetSkin < 0 || presetSkin > 0x7FFFFFFF) {
			throw new IllegalArgumentException("Invalid preset skin id: " + presetSkin);
		}
		return InternUtils.getPresetSkin(presetSkin);
	}

	public static IEaglerPlayerSkin loadPresetSkin(EnumPresetSkins presetSkin) {
		return InternUtils.getPresetSkin(presetSkin.getId());
	}

	public static IEaglerPlayerSkin loadPresetSkin(UUID playerUUID) {
		return InternUtils.getPresetSkin((playerUUID.hashCode() & 1) != 0 ? 1 : 0);
	}

	public static IEaglerPlayerSkin rewriteCustomSkinModelId(IEaglerPlayerSkin skin, int modelId) {
		if(modelId < 0 || modelId >= 0xFF) {
			throw new IllegalArgumentException("Invalid model id: " + modelId);
		}
		if(skin instanceof IModelRewritable rw) {
			return rw.rewriteModelInternal(modelId);
		}else {
			return skin;
		}
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

	public static IEaglerPlayerSkin loadSkinImageData64x64(int[] pixelsARGB8, int modelId) {
		if(pixelsARGB8.length != 4096) {
			throw new IllegalArgumentException("Skin data is the wrong length, should be 4096");
		}
		if(modelId < 0 || modelId >= 0xFF) {
			throw new IllegalArgumentException("Invalid model id: " + modelId);
		}
		byte[] tmp = new byte[12288];
		SkinConverterExt.convertToBytes(pixelsARGB8, tmp);
		return CustomSkinGeneric.createV4(modelId, tmp);
	}

	public static IEaglerPlayerSkin loadSkinImageData64x64(byte[] pixelsRGBA8, int modelId) {
		if(pixelsRGBA8.length != 16384) {
			throw new IllegalArgumentException("Skin data is the wrong length, should be 16384");
		}
		if(modelId < 0 || modelId >= 0xFF) {
			throw new IllegalArgumentException("Invalid model id: " + modelId);
		}
		return CustomSkinGeneric.createV3(modelId, pixelsRGBA8);
	}

	public static IEaglerPlayerSkin loadSkinImageData64x64Eagler(byte[] pixelsEagler, int modelId) {
		if(pixelsEagler.length != 12288) {
			throw new IllegalArgumentException("Skin data is the wrong length, should be 12288");
		}
		if(modelId < 0 || modelId >= 0xFF) {
			throw new IllegalArgumentException("Invalid model id: " + modelId);
		}
		return CustomSkinGeneric.createV4(modelId, pixelsEagler);
	}

	public static IEaglerPlayerSkin loadSkinImageData64x32(int[] pixelsARGB8, int modelId) {
		if(pixelsARGB8.length != 2048) {
			throw new IllegalArgumentException("Skin data is the wrong length, should be 2048");
		}
		if(modelId < 0 || modelId >= 0xFF) {
			throw new IllegalArgumentException("Invalid model id: " + modelId);
		}
		byte[] tmp = new byte[12288];
		SkinConverterExt.convert64x32To64x64(pixelsARGB8, tmp);
		return CustomSkinGeneric.createV4(modelId, tmp);
	}

	public static IEaglerPlayerSkin loadSkinImageData(BufferedImage image, int modelId) {
		if(image.getWidth() == 64) {
			if(image.getHeight() == 64) {
				int[] tmp = new int[4096];
				image.getRGB(0, 0, 64, 64, tmp, 0, 64);
				return loadSkinImageData64x64(tmp, modelId);
			}else if(image.getHeight() == 32) {
				int[] tmp = new int[2048];
				image.getRGB(0, 0, 64, 32, tmp, 0, 64);
				return loadSkinImageData64x32(tmp, modelId);
			}
		}
		throw new IllegalArgumentException("Image is the wrong size, should be 64x64 or 64x32");
	}

	public static IEaglerPlayerSkin loadSkinImageData(InputStream inputStream, int modelId) throws IOException {
		if(modelId < 0 || modelId >= 0xFF) {
			throw new IllegalArgumentException("Invalid model id: " + modelId);
		}
		return loadSkinImageData(ImageIO.read(inputStream), modelId);
	}

	public static IEaglerPlayerSkin loadSkinImageData(File imageFile, int modelId) throws IOException {
		if(modelId < 0 || modelId >= 0xFF) {
			throw new IllegalArgumentException("Invalid model id: " + modelId);
		}
		try(InputStream is = new FileInputStream(imageFile)) {
			return loadSkinImageData(is, modelId);
		}
	}

	public static IEaglerPlayerCape loadCapeImageData64x32(int[] pixelsARGB8) {
		if(pixelsARGB8.length != 2048) {
			throw new IllegalArgumentException("Cape data is the wrong length, should be 2048");
		}
		byte[] tmp = new byte[1173];
		SkinConverterExt.convertCape64x32RGBAto23x17RGB(pixelsARGB8, tmp);
		return new CustomCapeGeneric(tmp);
	}

	public static IEaglerPlayerCape loadCapeImageData32x32(int[] pixelsARGB8) {
		if(pixelsARGB8.length != 1024) {
			throw new IllegalArgumentException("Cape data is the wrong length, should be 1024");
		}
		byte[] tmp = new byte[1173];
		SkinConverterExt.convertCape32x32ARGBto23x17RGB(pixelsARGB8, tmp);
		return new CustomCapeGeneric(tmp);
	}

	public static IEaglerPlayerCape loadCapeImageData32x32(byte[] pixelsRGBA8) {
		if(pixelsRGBA8.length != 4096) {
			throw new IllegalArgumentException("Cape data is the wrong length, should be 4096");
		}
		byte[] tmp = new byte[1173];
		SkinConverterExt.convertCape32x32ABGRto23x17RGB(pixelsRGBA8, tmp);
		return new CustomCapeGeneric(tmp);
	}

	public static IEaglerPlayerCape loadCapeImageData23x17Eagler(byte[] pixelsEagler) {
		if(pixelsEagler.length != 1173) {
			throw new IllegalArgumentException("Cape data is the wrong length, should be 1173");
		}
		return new CustomCapeGeneric(pixelsEagler);
	}

	public static IEaglerPlayerCape loadCapeImageData(BufferedImage image) {
		if(image.getHeight() == 32) {
			if(image.getWidth() == 64) {
				int[] tmp = new int[2048];
				image.getRGB(0, 0, 64, 32, tmp, 0, 64);
				return loadCapeImageData64x32(tmp);
			}else if(image.getWidth() == 32) {
				int[] tmp = new int[1024];
				image.getRGB(0, 0, 32, 32, tmp, 0, 32);
				return loadCapeImageData32x32(tmp);
			}
		}
		throw new IllegalArgumentException("Image is the wrong size, should be 64x32 or 32x32");
	}

	public static IEaglerPlayerCape loadCapeImageData(InputStream inputStream) throws IOException {
		return loadCapeImageData(ImageIO.read(inputStream));
	}

	public static IEaglerPlayerCape loadCapeImageData(File imageFile) throws IOException {
		try(InputStream is = new FileInputStream(imageFile)) {
			return loadCapeImageData(is);
		}
	}

}
