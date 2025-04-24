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

public class SkinConverterExt {

	public static void convertToBytes(int[] imageIn, byte[] imageOut) {
		for(int i = 0, j, k; i < 4096; ++i) {
			j = i * 3;
			k = imageIn[i];
			imageOut[j] = (byte) k;
			imageOut[j + 1] = (byte)(k >>> 8);
			imageOut[j + 2] = (byte)(((k >>> 17) & 0x7F) | ((k >>> 24) & 0x80));
		}
	}

	public static void convert64x32To64x64(int[] imageIn, byte[] imageOut) {
		copyRawPixels(imageIn, imageOut, 0, 0, 0, 0, 64, 32, 64, 64, false);
		copyRawPixels(imageIn, imageOut, 24, 48, 20, 52, 4, 16, 8, 20, 64, 64);
		copyRawPixels(imageIn, imageOut, 28, 48, 24, 52, 8, 16, 12, 20, 64, 64);
		copyRawPixels(imageIn, imageOut, 20, 52, 16, 64, 8, 20, 12, 32, 64, 64);
		copyRawPixels(imageIn, imageOut, 24, 52, 20, 64, 4, 20, 8, 32, 64, 64);
		copyRawPixels(imageIn, imageOut, 28, 52, 24, 64, 0, 20, 4, 32, 64, 64);
		copyRawPixels(imageIn, imageOut, 32, 52, 28, 64, 12, 20, 16, 32, 64, 64);
		copyRawPixels(imageIn, imageOut, 40, 48, 36, 52, 44, 16, 48, 20, 64, 64);
		copyRawPixels(imageIn, imageOut, 44, 48, 40, 52, 48, 16, 52, 20, 64, 64);
		copyRawPixels(imageIn, imageOut, 36, 52, 32, 64, 48, 20, 52, 32, 64, 64);
		copyRawPixels(imageIn, imageOut, 40, 52, 36, 64, 44, 20, 48, 32, 64, 64);
		copyRawPixels(imageIn, imageOut, 44, 52, 40, 64, 40, 20, 44, 32, 64, 64);
		copyRawPixels(imageIn, imageOut, 48, 52, 44, 64, 52, 20, 56, 32, 64, 64);
	}

	private static void copyRawPixels(int[] imageIn, byte[] imageOut, int dx1, int dy1, int dx2, int dy2, int sx1,
			int sy1, int sx2, int sy2, int imgSrcWidth, int imgDstWidth) {
		if(dx1 > dx2) {
			copyRawPixels(imageIn, imageOut, sx1, sy1, dx2, dy1, sx2 - sx1, sy2 - sy1, imgSrcWidth, imgDstWidth, true);
		} else {
			copyRawPixels(imageIn, imageOut, sx1, sy1, dx1, dy1, sx2 - sx1, sy2 - sy1, imgSrcWidth, imgDstWidth, false);
		}
	}

	private static void copyRawPixels(int[] imageIn, byte[] imageOut, int srcX, int srcY, int dstX, int dstY, int width,
			int height, int imgSrcWidth, int imgDstWidth, boolean flip) {
		int i, j;
		for(int y = 0; y < height; ++y) {
			for(int x = 0; x < width; ++x) {
				i = imageIn[(srcY + y) * imgSrcWidth + srcX + x];
				if(flip) {
					j = (dstY + y) * imgDstWidth + dstX + width - x - 1;
				}else {
					j = (dstY + y) * imgDstWidth + dstX + x;
				}
				j = j * 3;
				imageOut[j] = (byte)i;
				imageOut[j + 1] = (byte)(i >>> 8);
				imageOut[j + 2] = (byte)(((i >>> 17) & 0x7F) | ((i >>> 24) & 0x80));
			}
		}
	}

	public static void convertCape64x32RGBAto23x17RGB(int[] skinIn, byte[] skinOut) {
		int i, j;
		for(int y = 0; y < 17; ++y) {
			for(int x = 0; x < 22; ++x) {
				i = skinIn[y * 64 + x];
				j = (y * 23 + x) * 3;
				skinOut[j] = (byte)(i >>> 16);
				skinOut[j + 1] = (byte)(i >>> 8);
				skinOut[j + 2] = (byte)i;
			}
		}
		for(int y = 0; y < 11; ++y) {
			i = skinIn[(y + 11) * 64 + 22];
			j = ((y + 6) * 23 + 22) * 3;
			skinOut[j] = (byte)(i >>> 16);
			skinOut[j + 1] = (byte)(i >>> 8);
			skinOut[j + 2] = (byte)i;
		}
	}

	public static byte[] convertToV3Raw(byte[] v4data) {
		byte[] v3data = new byte[16384];
		for(int i = 0, j, k; i < 4096; ++i) {
			j = i * 3;
			k = i << 2;
			v3data[k + 1] = v4data[j];
			v3data[k + 2] = v4data[j + 1];
			v3data[k + 3] = (byte)((v4data[j + 2] & 0x7F) << 1);
			v3data[k] = (v4data[j + 2] & 0x80) != 0 ? (byte)0xFF : (byte)0;
		}
		return v3data;
	}

	public static byte[] convertToV4Raw(byte[] v3data) {
		byte[] v4data = new byte[12288];
		for(int i = 0, j, k; i < 4096; ++i) {
			j = i << 2;
			k = i * 3;
			v4data[k] = v3data[j + 1];
			v4data[k + 1] = v3data[j + 2];
			v4data[k + 2] = (byte)(((v3data[j + 3] & 0xFF) >>> 1) | (v3data[j] & 0x80));
		}
		return v4data;
	}

	public static void convertCape23x17RGBto32x32ABGR(byte[] skinIn, byte[] skinOut) {
		convertCape23x17RGBto32x32ABGR(skinIn, 0, skinOut, 0);
	}

	public static void convertCape23x17RGBto32x32ABGR(byte[] skinIn, int inOffset, byte[] skinOut, int outOffset) {
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

	public static void convertCape32x32ABGRto23x17RGB(byte[] skinIn, byte[] skinOut) {
		convertCape32x32ABGRto23x17RGB(skinIn, 0, skinOut, 0);
	}

	public static void convertCape32x32ABGRto23x17RGB(byte[] skinIn, int inOffset, byte[] skinOut, int outOffset) {
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

	public static void convertCape32x32ARGBto23x17RGB(int[] skinIn, byte[] skinOut) {
		int i, j;
		for(int y = 0; y < 17; ++y) {
			for(int x = 0; x < 22; ++x) {
				i = skinIn[y * 32 + x];
				j = (y * 23 + x) * 3;
				skinOut[j] = (byte)i;
				skinOut[j + 1] = (byte)(i >>> 8);
				skinOut[j + 2] = (byte)(i >>> 16);
			}
		}
		for(int y = 0; y < 11; ++y) {
			i = skinIn[(y + 11) * 32 + 22];
			j = ((y + 6) * 23 + 22) * 3;
			skinOut[j] = (byte)i;
			skinOut[j + 1] = (byte)(i >>> 8);
			skinOut[j + 2] = (byte)(i >>> 16);
		}
	}

}
