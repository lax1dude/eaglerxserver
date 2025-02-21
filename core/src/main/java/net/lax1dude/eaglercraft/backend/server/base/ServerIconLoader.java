package net.lax1dude.eaglercraft.backend.server.base;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.lax1dude.eaglercraft.backend.server.api.IServerIconLoader;

public class ServerIconLoader {

	public static final IServerIconLoader INSTANCE = new IServerIconLoader() {
		@Override
		public int[] loadServerIcon(int[] pixelsIn, int width, int height) {
			return ServerIconLoader.loadServerIcon(pixelsIn, width, height);
		}
		@Override
		public int[] loadServerIcon(BufferedImage image) {
			return ServerIconLoader.loadServerIcon(image);
		}
		@Override
		public int[] loadServerIcon(InputStream stream) throws IOException {
			return ServerIconLoader.loadServerIcon(stream);
		}
		@Override
		public int[] loadServerIcon(File file) throws IOException {
			return ServerIconLoader.loadServerIcon(file);
		}
	};

	public static int[] loadServerIcon(int[] pixelsIn, int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	public static int[] loadServerIcon(BufferedImage image) {
		// TODO Auto-generated method stub
		return null;
	}

	public static int[] loadServerIcon(InputStream stream) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public static int[] loadServerIcon(File file) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
