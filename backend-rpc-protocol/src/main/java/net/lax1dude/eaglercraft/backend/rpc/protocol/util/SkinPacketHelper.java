/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.rpc.protocol.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class SkinPacketHelper {

	public static byte[] writePresetSkinPacket(int presetId) {
		byte[] tex = new byte[5];
		tex[0] = (byte)1;
		tex[1] = (byte)(presetId >>> 24);
		tex[2] = (byte)(presetId >>> 16);
		tex[3] = (byte)(presetId >>> 8);
		tex[4] = (byte)(presetId & 0xFF);
		return tex;
	}

	public static byte[] loadCustomSkin(File texture64x64) throws IOException {
		return loadCustomSkin(ImageIO.read(texture64x64));
	}

	public static byte[] loadCustomSkin(InputStream texture64x64) throws IOException {
		return loadCustomSkin(ImageIO.read(texture64x64));
	}

	public static byte[] loadCustomSkin(BufferedImage texture64x64) {
		if(texture64x64.getWidth() != 64 || texture64x64.getHeight() != 64) {
			throw new IllegalArgumentException("Image is not 64x64!");
		}
		byte[] tex = new byte[16384];
		for(int y = 0; y < 64; ++y) {
			for(int x = 0; x < 64; ++x) {
				int idx = (y << 8) | (x << 2);
				int rgba = texture64x64.getRGB(x, y);
				tex[idx] = (byte)(rgba >>> 24);
				tex[idx + 1] = (byte)(rgba & 0xFF);
				tex[idx + 2] = (byte)(rgba >>> 8);
				tex[idx + 3] = (byte)(rgba >>> 16);
			}
		}
		return tex;
	}

	public static byte[] writeCustomSkinPacket(int modelId, byte[] texture64x64) {
		if(texture64x64.length != 16384) {
			throw new IllegalArgumentException("Wrong array length for 64x64 RGBA texture!");
		}
		byte[] tex = new byte[2 + texture64x64.length];
		tex[0] = (byte)2;
		tex[1] = (byte)modelId;
		System.arraycopy(texture64x64, 0, tex, 2, texture64x64.length);
		return tex;
	}

	public static byte[] writeCustomSkinPacket(int modelId, File texture64x64) throws IOException {
		return writeCustomSkinPacket(modelId, ImageIO.read(texture64x64));
	}

	public static byte[] writeCustomSkinPacket(int modelId, InputStream texture64x64) throws IOException {
		return writeCustomSkinPacket(modelId, ImageIO.read(texture64x64));
	}

	public static byte[] writeCustomSkinPacket(int modelId, BufferedImage texture64x64) {
		if(texture64x64.getWidth() != 64 || texture64x64.getHeight() != 64) {
			throw new IllegalArgumentException("Image is not 64x64!");
		}
		byte[] tex = new byte[16386];
		tex[0] = (byte)2;
		tex[1] = (byte)modelId;
		for(int y = 0; y < 64; ++y) {
			for(int x = 0; x < 64; ++x) {
				int idx = (y << 8) | (x << 2);
				int rgba = texture64x64.getRGB(x, y);
				tex[idx + 2] = (byte)(rgba >>> 24);
				tex[idx + 3] = (byte)(rgba & 0xFF);
				tex[idx + 4] = (byte)(rgba >>> 8);
				tex[idx + 5] = (byte)(rgba >>> 16);
			}
		}
		return tex;
	}

	public static byte[] writePresetCapePacket(int presetId) {
		byte[] tex = new byte[5];
		tex[0] = (byte)1;
		tex[1] = (byte)(presetId >>> 24);
		tex[2] = (byte)(presetId >>> 16);
		tex[3] = (byte)(presetId >>> 8);
		tex[4] = (byte)(presetId & 0xFF);
		return tex;
	}

	public static byte[] loadCustomCape(File textureNx32) throws IOException {
		return loadCustomCape(ImageIO.read(textureNx32));
	}

	public static byte[] loadCustomCape(InputStream textureNx32) throws IOException {
		return loadCustomCape(ImageIO.read(textureNx32));
	}

	public static byte[] loadCustomCape(BufferedImage textureNx32) {
		if((textureNx32.getWidth() != 32 && textureNx32.getWidth() != 64) || textureNx32.getHeight() != 32) {
			throw new IllegalArgumentException("Image is not 32x32 or 64x32!");
		}
		byte[] tex = new byte[4096];
		for(int y = 0; y < 32; ++y) {
			for(int x = 0; x < 32; ++x) {
				int idx = (y << 7) | (x << 2);
				int rgba = textureNx32.getRGB(x, y);
				tex[idx] = (byte)(rgba >>> 24);
				tex[idx + 1] = (byte)(rgba & 0xFF);
				tex[idx + 2] = (byte)(rgba >>> 8);
				tex[idx + 3] = (byte)(rgba >>> 16);
			}
		}
		return tex;
	}

	public static byte[] writeCustomCapePacket(File textureNx32) throws IOException {
		return writeCustomCapePacket(ImageIO.read(textureNx32));
	}

	public static byte[] writeCustomCapePacket(InputStream textureNx32) throws IOException {
		return writeCustomCapePacket(ImageIO.read(textureNx32));
	}

	public static byte[] writeCustomCapePacket(BufferedImage textureNx32) {
		byte[] tex = loadCustomCape(textureNx32);
		byte[] ret = new byte[1174];
		ret[0] = (byte)2;
		convertCape32x32RGBAto23x17RGB(tex, 0, ret, 1);
		return ret; 
	}

	public static byte[] writeCustomCapePacket(byte[] texture32x32) {
		if(texture32x32.length != 4096) {
			throw new IllegalArgumentException("Wrong array length for 32x32 RGBA texture!");
		}
		byte[] tex = new byte[1174];
		tex[0] = (byte)2;
		convertCape32x32RGBAto23x17RGB(texture32x32, 0, tex, 1);
		return tex;
	}

	public static void convertCape32x32RGBAto23x17RGB(byte[] skinIn, byte[] skinOut) {
		convertCape32x32RGBAto23x17RGB(skinIn, 0, skinOut, 0);
	}

	public static void convertCape32x32RGBAto23x17RGB(byte[] skinIn, int inOffset, byte[] skinOut, int outOffset) {
		int i, j;
		for(int y = 0; y < 17; ++y) {
			for(int x = 0; x < 22; ++x) {
				i = inOffset + ((y * 32 + x) << 2);
				j = outOffset + ((y * 23 + x) * 3);
				skinOut[j] = skinIn[i + 1];
				skinOut[j + 1] = skinIn[i + 2];
				skinOut[j + 2] = skinIn[i + 3];
			}
		}
		for(int y = 0; y < 11; ++y) {
			i = inOffset + (((y + 11) * 32 + 22) << 2);
			j = outOffset + (((y + 6) * 23 + 22) * 3);
			skinOut[j] = skinIn[i + 1];
			skinOut[j + 1] = skinIn[i + 2];
			skinOut[j + 2] = skinIn[i + 3];
		}
	}

	public static void convertCape23x17RGBto32x32RGBA(byte[] skinIn, byte[] skinOut) {
		convertCape23x17RGBto32x32RGBA(skinIn, 0, skinOut, 0);
	}

	public static void convertCape23x17RGBto32x32RGBA(byte[] skinIn, int inOffset, byte[] skinOut, int outOffset) {
		int i, j;
		for(int y = 0; y < 17; ++y) {
			for(int x = 0; x < 22; ++x) {
				i = outOffset + ((y * 32 + x) << 2);
				j = inOffset + ((y * 23 + x) * 3);
				skinOut[i] = (byte)0xFF;
				skinOut[i + 1] = skinIn[j];
				skinOut[i + 2] = skinIn[j + 1];
				skinOut[i + 3] = skinIn[j + 2];
			}
		}
		for(int y = 0; y < 11; ++y) {
			i = outOffset + (((y + 11) * 32 + 22) << 2);
			j = inOffset + (((y + 6) * 23 + 22) * 3);
			skinOut[i] = (byte)0xFF;
			skinOut[i + 1] = skinIn[j];
			skinOut[i + 2] = skinIn[j + 1];
			skinOut[i + 3] = skinIn[j + 2];
		}
	}

}