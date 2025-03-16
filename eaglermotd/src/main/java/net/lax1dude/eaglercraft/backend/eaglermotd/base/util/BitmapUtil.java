package net.lax1dude.eaglercraft.backend.eaglermotd.base.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class BitmapUtil {

	private final Map<String, Bitmap> bitmapCache;

	public BitmapUtil() {
		this.bitmapCache = new HashMap<>();
	}

	public Bitmap getCachedIcon(String name) throws IOException {
		Bitmap ret = bitmapCache.get(name);
		if(ret == null) {
			BufferedImage img = ImageIO.read(new File(name));
			int w = img.getWidth();
			int h = img.getHeight();
			int[] arr = new int[w * h];
			img.getRGB(0, 0, w, h, arr, 0, w);
			bitmapCache.put(name, ret = new Bitmap(arr, w, h));
		}
		return ret;
	}

	public static class Bitmap {

		private final int[] pixels;
		private final int w;
		private final int h;

		private Bitmap(int[] pixels, int w, int h) {
			this.pixels = pixels;
			this.w = w;
			this.h = h;
		}


		public int[] getSprite(int x, int y) {
			if(x < 0 || y < 0) {
				return null;
			}
			int offsetX = x;
			int offsetY = y;
			if(offsetX + 64 > w || offsetY + 64 > h) {
				return null;
			}
			int[] ret = new int[64 * 64];
			for(int i = 0; i < ret.length; ++i) {
				int xx = i % 64;
				int yy = i / 64;
				ret[i] = pixels[(offsetY + yy) * w + offsetX + xx];
			}
			return ret;
		}

	}
	
	public static int[] makeColor(int[] in, float r, float g, float b, float a) {
		if(r < 0.0f) r = 0.0f;
		if(r > 1.0f) r = 1.0f;
		if(g < 0.0f) g = 0.0f;
		if(g > 1.0f) g = 1.0f;
		if(b < 0.0f) b = 0.0f;
		if(b > 1.0f) b = 1.0f;
		if(a < 0.0f) a = 0.0f;
		if(a > 1.0f) a = 1.0f;
		int c = ((int)(a*255.0f) << 24) | ((int)(r*255.0f) << 16) | ((int)(g*255.0f) << 8) | (int)(b*255.0f);
		for(int i = 0; i < in.length; ++i) {
			in[i] = c;
		}
		return in;
	}
	
	public static int[] applyColor(int[] in, float r, float g, float b, float a) {
		for(int i = 0; i < in.length; ++i) {
			float rr = ((in[i] >> 16) & 0xFF) / 255.0f;
			float gg = ((in[i] >> 8) & 0xFF) / 255.0f;
			float bb = (in[i] & 0xFF) / 255.0f;
			float aa = ((in[i] >> 24) & 0xFF) / 255.0f;
			rr = r * a + rr * (1.0f - a);
			gg = g * a + gg * (1.0f - a);
			bb = b * a + bb * (1.0f - a);
			aa = a + aa * (1.0f - a);
			if(rr < 0.0f) rr = 0.0f;
			if(rr > 1.0f) rr = 1.0f;
			if(gg < 0.0f) gg = 0.0f;
			if(gg > 1.0f) gg = 1.0f;
			if(bb < 0.0f) bb = 0.0f;
			if(bb > 1.0f) bb = 1.0f;
			if(aa < 0.0f) aa = 0.0f;
			if(aa > 1.0f) aa = 1.0f;
			in[i] = ((int)(aa*255.0f) << 24) | ((int)(rr*255.0f) << 16) | ((int)(gg*255.0f) << 8) | (int)(bb*255.0f);
		}
		return in;
	}
	
	public static int[] applyTint(int[] in, float r, float g, float b, float a) {
		for(int i = 0; i < in.length; ++i) {
			float rr = ((in[i] >> 16) & 0xFF) / 255.0f * r;
			float gg = ((in[i] >> 8) & 0xFF) / 255.0f * g;
			float bb = (in[i] & 0xFF) / 255.0f * b;
			float aa = ((in[i] >> 24) & 0xFF) / 255.0f * a;
			if(rr < 0.0f) rr = 0.0f;
			if(rr > 1.0f) rr = 1.0f;
			if(gg < 0.0f) gg = 0.0f;
			if(gg > 1.0f) gg = 1.0f;
			if(bb < 0.0f) bb = 0.0f;
			if(bb > 1.0f) bb = 1.0f;
			if(aa < 0.0f) aa = 0.0f;
			if(aa > 1.0f) aa = 1.0f;
			in[i] = ((int)(aa*255.0f) << 24) | ((int)(rr*255.0f) << 16) | ((int)(gg*255.0f) << 8) | (int)(bb*255.0f);
		}
		return in;
	}
	

	public static int[] flipX(int[] newIcon) {
		int[] tmp = new int[64];
		for(int y = 0; y < 64; ++y) {
			int o = y * 64;
			System.arraycopy(newIcon, o, tmp, 0, 64);
			for(int i = 0; i < 64; ++i) {
				newIcon[o + i] = tmp[63 - i];
			}
		}
		return newIcon;
	}

	public static int[] flipY(int[] newIcon) {
		int[] tmp = new int[64];
		for(int x = 0; x < 64; ++x) {
			for(int i = 0; i < 64; ++i) {
				tmp[i] = newIcon[i * 64 + x];
			}
			for(int i = 0; i < 64; ++i) {
				newIcon[i * 64 + x] = tmp[63 - i];
			}
		}
		return newIcon;
	}
	
	private static int[] transpose(int[] in) {
		int[] ret = new int[64*64];
		for(int y = 0; y < 64; ++y) {
			for(int x = 0; x < 64; ++x) {
				ret[x * 64 + y] = in[y * 64 + x];
			}
		}
		return ret;
	}

	public static int[] rotate(int[] newIcon, int rotate) {
		rotate = rotate % 4;
		if(rotate == 1) {
			newIcon = flipX(transpose(newIcon));
		}else if(rotate == 2) {
			newIcon = flipY(flipX(newIcon));
		}else if(rotate == 3) {
			newIcon = flipY(transpose(newIcon));
		}
		return newIcon;
	}

	public static byte[] toBytes(int[] iconIn) {
		byte[] iconPixels = new byte[16384];
		for(int i = 0, j; i < 4096; ++i) {
			j = i << 2;
			iconPixels[j] = (byte)(iconIn[i] >>> 16);
			iconPixels[j + 1] = (byte)(iconIn[i] >>> 8);
			iconPixels[j + 2] = (byte)(iconIn[i] & 0xFF);
			iconPixels[j + 3] = (byte)(iconIn[i] >>> 24);
		}
		return iconPixels;
	}

}
