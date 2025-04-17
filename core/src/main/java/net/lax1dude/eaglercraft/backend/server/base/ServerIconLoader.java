package net.lax1dude.eaglercraft.backend.server.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.lax1dude.eaglercraft.backend.server.api.IServerIconLoader;

public class ServerIconLoader {

	public static final IServerIconLoader INSTANCE = new IServerIconLoader() {
		@Override
		public byte[] loadServerIcon(int[] pixelsIn, int width, int height) {
			return ServerIconLoader.loadServerIcon(pixelsIn, width, height);
		}
		@Override
		public byte[] loadServerIcon(BufferedImage image) {
			return ServerIconLoader.loadServerIcon(image);
		}
		@Override
		public byte[] loadServerIcon(InputStream stream) throws IOException {
			return ServerIconLoader.loadServerIcon(stream);
		}
		@Override
		public byte[] loadServerIcon(File file) throws IOException {
			return ServerIconLoader.loadServerIcon(file);
		}
	};

	public static byte[] loadServerIcon(int[] pixelsIn, int width, int height) {
		if(pixelsIn == null) {
			throw new NullPointerException("pixelsIn");
		}
		if(width == 64 && height == 64) {
			return toBytes(pixelsIn);
		}else {
			BufferedImage tmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			tmp.setRGB(0, 0, width, height, pixelsIn, 0, width);
			return loadServerIcon(tmp);
		}
	}

	public static byte[] loadServerIcon(BufferedImage image) {
		BufferedImage icon = image;
		boolean gotScaled = false;
		if(icon.getWidth() != 64 || icon.getHeight() != 64) {
			icon = new BufferedImage(64, 64, image.getType());
			Graphics2D g = (Graphics2D) icon.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, (image.getWidth() < 64 || image.getHeight() < 64) ?
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR : RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setBackground(new Color(0, true));
			g.clearRect(0, 0, 64, 64);
			int ow = image.getWidth();
			int oh = image.getHeight();
			int nw, nh;
			float aspectRatio = (float)oh / (float)ow;
			if(aspectRatio >= 1.0f) {
				nw = (int)(64 / aspectRatio);
				nh = 64;
			}else {
				nw = 64;
				nh = (int)(64 * aspectRatio);
			}
			g.drawImage(image, (64 - nw) / 2, (64 - nh) / 2, (64 - nw) / 2 + nw, (64 - nh) / 2 + nh, 0, 0, image.getWidth(), image.getHeight(), null);
			g.dispose();
			gotScaled = true;
		}
		int[] pxls = icon.getRGB(0, 0, 64, 64, new int[4096], 0, 64);
		if(gotScaled) {
			for(int i = 0; i < pxls.length; ++i) {
				if((pxls[i] & 0xFFFFFF) == 0) {
					pxls[i] = 0;
				}
			}
		}
		return toBytes(pxls);
	}

	public static byte[] loadServerIcon(InputStream stream) throws IOException {
		return loadServerIcon(ImageIO.read(stream));
	}

	public static byte[] loadServerIcon(File file) throws IOException {
		return loadServerIcon(ImageIO.read(file));
	}

	private static byte[] toBytes(int[] pixels) {
		byte[] iconPixels = new byte[16384];
		for(int i = 0, j; i < 4096; ++i) {
			j = i << 2;
			iconPixels[j] = (byte)(pixels[i] >>> 16);
			iconPixels[j + 1] = (byte)(pixels[i] >>> 8);
			iconPixels[j + 2] = (byte)(pixels[i] & 0xFF);
			iconPixels[j + 3] = (byte)(pixels[i] >>> 24);
		}
		return iconPixels;
	}

}
