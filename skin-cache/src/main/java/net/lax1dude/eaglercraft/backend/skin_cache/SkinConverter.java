/*
 * Copyright (c) 2022-2025 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.backend.skin_cache;

public class SkinConverter {

	public static void convertToBytes(int[] imageIn, byte[] imageOut) {
		for(int i = 0, j, k; i < 4096; ++i) {
			j = i * 3;
			k = imageIn[i];
			imageOut[j] = (byte)(k & 0xFF);
			imageOut[j + 1] = (byte)(k >>> 8);
			imageOut[j + 2] = (byte)(((k >>> 17) & 0x7F) | ((k >>> 24) & 0x80));
		}
		setAlphaForChestV4(imageOut);
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
		setAlphaForChestV4(imageOut);
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
				imageOut[j] = (byte)(i & 0xFF);
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
				skinOut[j + 2] = (byte)(i & 0xFF);
			}
		}
		for(int y = 0; y < 11; ++y) {
			i = skinIn[(y + 11) * 64 + 22];
			j = ((y + 6) * 23 + 22) * 3;
			skinOut[j] = (byte)(i >>> 16);
			skinOut[j + 1] = (byte)(i >>> 8);
			skinOut[j + 2] = (byte)(i & 0xFF);
		}
	}

	public static void setAlphaForChestV4(byte[] skin64x64) {
		for(int y = 20; y < 32; ++y) {
			for(int x = 16; x < 40; ++x) {
				skin64x64[((y << 6) | x) * 3] |= 0x80;
			}
		}
	}

}