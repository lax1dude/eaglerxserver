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

package net.lax1dude.eaglercraft.backend.server.base.skins;

public class SkinConverterExt {

	public static void convertCape23x17RGBto32x32ABGR(byte[] skinIn, byte[] skinOut) {
		convertCape23x17RGBto32x32ABGR(skinIn, 0, skinOut, 0);
	}

	public static void convertCape23x17RGBto32x32ABGR(byte[] skinIn, int inOffset, byte[] skinOut, int outOffset) {
		int i, j;
		for (int y = 0; y < 17; ++y) {
			for (int x = 0; x < 22; ++x) {
				i = outOffset + ((y * 32 + x) << 2);
				j = inOffset + ((y * 23 + x) * 3);
				skinOut[i] = (byte) 0xFF;
				skinOut[i + 1] = skinIn[j];
				skinOut[i + 2] = skinIn[j + 1];
				skinOut[i + 3] = skinIn[j + 2];
			}
		}
		for (int y = 0; y < 11; ++y) {
			i = outOffset + (((y + 11) * 32 + 22) << 2);
			j = inOffset + (((y + 6) * 23 + 22) * 3);
			skinOut[i] = (byte) 0xFF;
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
		for (int y = 0; y < 17; ++y) {
			for (int x = 0; x < 22; ++x) {
				i = inOffset + ((y * 32 + x) << 2);
				j = outOffset + ((y * 23 + x) * 3);
				skinOut[j] = skinIn[i + 1];
				skinOut[j + 1] = skinIn[i + 2];
				skinOut[j + 2] = skinIn[i + 3];
			}
		}
		for (int y = 0; y < 11; ++y) {
			i = inOffset + (((y + 11) * 32 + 22) << 2);
			j = outOffset + (((y + 6) * 23 + 22) * 3);
			skinOut[j] = skinIn[i + 1];
			skinOut[j + 1] = skinIn[i + 2];
			skinOut[j + 2] = skinIn[i + 3];
		}
	}

	public static void convertCape32x32ARGBto23x17RGB(int[] skinIn, byte[] skinOut) {
		int i, j;
		for (int y = 0; y < 17; ++y) {
			for (int x = 0; x < 22; ++x) {
				i = skinIn[y * 32 + x];
				j = (y * 23 + x) * 3;
				skinOut[j] = (byte) i;
				skinOut[j + 1] = (byte) (i >>> 8);
				skinOut[j + 2] = (byte) (i >>> 16);
			}
		}
		for (int y = 0; y < 11; ++y) {
			i = skinIn[(y + 11) * 32 + 22];
			j = ((y + 6) * 23 + 22) * 3;
			skinOut[j] = (byte) i;
			skinOut[j + 1] = (byte) (i >>> 8);
			skinOut[j + 2] = (byte) (i >>> 16);
		}
	}

}
