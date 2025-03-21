package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import io.netty.buffer.ByteBuf;

class SkinHandshakeConverter {

	static void convertSkinPixels(ByteBuf imageIn, int offsetIn, byte[] imageOut, int offsetOut, int count) {
		for(int i = 0, j, k, l = count << 2; i < l; i += 4) {
			j = imageIn.getIntLE(offsetIn + i);
			k = offsetOut + i;
			imageOut[k] = (byte)(j >>> 24);
			imageOut[k + 1] = (byte)(j & 0xFF);
			imageOut[k + 2] = (byte)(j >>> 8);
			imageOut[k + 3] = (byte)(j >>> 16);
		}
	}

	/**
	 * Intended for handshake, write a ByteBuf-to-ByteBuf version for play packets!
	 */
	static void convertSkin64x32To64x64(ByteBuf imageIn, int offsetIn, byte[] imageOut, int offsetOut) {
		copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, 0, 0, 0, 0, 64, 32, 64, 64, false);
		copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, 24, 48, 20, 52, 4, 16, 8, 20, 64, 64);
		copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, 28, 48, 24, 52, 8, 16, 12, 20, 64, 64);
		copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, 20, 52, 16, 64, 8, 20, 12, 32, 64, 64);
		copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, 24, 52, 20, 64, 4, 20, 8, 32, 64, 64);
		copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, 28, 52, 24, 64, 0, 20, 4, 32, 64, 64);
		copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, 32, 52, 28, 64, 12, 20, 16, 32, 64, 64);
		copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, 40, 48, 36, 52, 44, 16, 48, 20, 64, 64);
		copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, 44, 48, 40, 52, 48, 16, 52, 20, 64, 64);
		copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, 36, 52, 32, 64, 48, 20, 52, 32, 64, 64);
		copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, 40, 52, 36, 64, 44, 20, 48, 32, 64, 64);
		copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, 44, 52, 40, 64, 40, 20, 44, 32, 64, 64);
		copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, 48, 52, 44, 64, 52, 20, 56, 32, 64, 64);
	}

	static void copyRawPixels(ByteBuf imageIn, int offsetIn, byte[] imageOut, int offsetOut, int dx1, int dy1,
			int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, int imgSrcWidth, int imgDstWidth) {
		if (dx1 > dx2) {
			copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, sx1, sy1, dx2, dy1, sx2 - sx1, sy2 - sy1, imgSrcWidth,
					imgDstWidth, true);
		} else {
			copyRawPixels(imageIn, offsetIn, imageOut, offsetOut, sx1, sy1, dx1, dy1, sx2 - sx1, sy2 - sy1, imgSrcWidth,
					imgDstWidth, false);
		}
	}

	private static void copyRawPixels(ByteBuf imageIn, int offsetIn, byte[] imageOut, int offsetOut, int srcX, int srcY,
			int dstX, int dstY, int width, int height, int imgSrcWidth, int imgDstWidth, boolean flip) {
		int i, j;
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				i = imageIn.getIntLE((((srcY + y) * imgSrcWidth + srcX + x) << 2) + offsetIn);
				if (flip) {
					j = (dstY + y) * imgDstWidth + dstX + width - x - 1;
				} else {
					j = (dstY + y) * imgDstWidth + dstX + x;
				}
				j = (j << 2) + offsetOut;
				imageOut[j] = (byte) (i >>> 24);
				imageOut[j + 1] = (byte) (i & 0xFF);
				imageOut[j + 2] = (byte) (i >>> 8);
				imageOut[j + 3] = (byte) (i >>> 16);
			}
		}
	}

	/**
	 * Intended for handshake, write a ByteBuf-to-ByteBuf version for play packets!
	 */
	static void convertCape32x32RGBAto23x17RGB(ByteBuf imageIn, int inOffset, byte[] skinOut, int outOffset) {
		int i, j;
		for(int y = 0; y < 17; ++y) {
			for(int x = 0; x < 22; ++x) {
				i = inOffset + ((y * 32 + x) << 2);
				j = outOffset + ((y * 23 + x) * 3);
				skinOut[j] = imageIn.getByte(i + 1);
				skinOut[j + 1] = imageIn.getByte(i + 2);
				skinOut[j + 2] = imageIn.getByte(i + 3);
			}
		}
		for(int y = 0; y < 11; ++y) {
			i = inOffset + (((y + 11) * 32 + 22) << 2);
			j = outOffset + (((y + 6) * 23 + 22) * 3);
			skinOut[j] = imageIn.getByte(i + 1);
			skinOut[j + 1] = imageIn.getByte(i + 2);
			skinOut[j + 2] = imageIn.getByte(i + 3);
		}
	}

}
