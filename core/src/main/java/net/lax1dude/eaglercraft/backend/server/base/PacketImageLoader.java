package net.lax1dude.eaglercraft.backend.server.base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public class PacketImageLoader {

	public static final IPacketImageLoader INSTANCE = new IPacketImageLoader() {
		@Override
		public PacketImageData loadPacketImageData(int[] pixelsARGB8, int width, int height) {
			return PacketImageLoader.loadPacketImageData(pixelsARGB8, width, height);
		}
		@Override
		public PacketImageData loadPacketImageData(BufferedImage bufferedImage, int maxWidth, int maxHeight) {
			return PacketImageLoader.loadPacketImageData(bufferedImage, maxWidth, maxHeight);
		}
		@Override
		public PacketImageData loadPacketImageData(InputStream inputStream, int maxWidth, int maxHeight) throws IOException {
			return PacketImageLoader.loadPacketImageData(inputStream, maxWidth, maxHeight);
		}
		@Override
		public PacketImageData loadPacketImageData(File imageFile, int maxWidth, int maxHeight) throws IOException {
			return PacketImageLoader.loadPacketImageData(imageFile, maxWidth, maxHeight);
		}
	};

	public static PacketImageData loadPacketImageData(int[] pixelsARGB8, int width, int height) {
		return new PacketImageData(width, height, pixelsARGB8);
	}

	public static PacketImageData loadPacketImageData(BufferedImage img, int maxWidth, int maxHeight) {
		int w = img.getWidth();
		int h = img.getHeight();
		if(w > maxWidth || h > maxHeight) {
			float aspectRatio = (float)w / (float)h;
			int nw, nh;
			if(aspectRatio >= 1.0f) {
				nw = (int)(maxWidth / aspectRatio);
				nh = maxHeight;
			}else {
				nw = maxWidth;
				nh = (int)(maxHeight * aspectRatio);
			}
			BufferedImage resized = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) resized.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setBackground(new Color(0, true));
			g.clearRect(0, 0, nw, nh);
			g.drawImage(img, 0, 0, nw, nh, 0, 0, w, h, null);
			g.dispose();
			img = resized;
		}
		int[] pixels = new int[w * h];
		img.getRGB(0, 0, w, h, pixels, 0, w);
		return new PacketImageData(w, h, pixels);
	}

	public static PacketImageData loadPacketImageData(InputStream inputStream, int maxWidth, int maxHeight) throws IOException {
		return loadPacketImageData(ImageIO.read(inputStream), maxWidth, maxHeight);
	}

	public static PacketImageData loadPacketImageData(File imageFile, int maxWidth, int maxHeight) throws IOException {
		return loadPacketImageData(ImageIO.read(imageFile), maxWidth, maxHeight);
	}

}
