package net.lax1dude.eaglercraft.backend.server.base;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
		// TODO Auto-generated method stub
		return null;
	}

	public static byte[] loadServerIcon(BufferedImage image) {
		// TODO Auto-generated method stub
		return null;
	}

	public static byte[] loadServerIcon(InputStream stream) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public static byte[] loadServerIcon(File file) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
