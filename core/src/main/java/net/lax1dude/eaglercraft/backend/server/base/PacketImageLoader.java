package net.lax1dude.eaglercraft.backend.server.base;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public class PacketImageLoader {

	public static final IPacketImageLoader INSTANCE = new IPacketImageLoader() {
		@Override
		public PacketImageData loadPacketImageData(int width, int height, int[] pixelsARGB8) {
			return PacketImageLoader.loadPacketImageData(width, height, pixelsARGB8);
		}
		@Override
		public PacketImageData loadPacketImageData(BufferedImage bufferedImage) {
			return PacketImageLoader.loadPacketImageData(bufferedImage);
		}
		@Override
		public PacketImageData loadPacketImageData(InputStream inputStream) throws IOException {
			return PacketImageLoader.loadPacketImageData(inputStream);
		}
		@Override
		public PacketImageData loadPacketImageData(File imageFile) throws IOException {
			return PacketImageLoader.loadPacketImageData(imageFile);
		}
	};

	public static PacketImageData loadPacketImageData(int width, int height, int[] pixelsARGB8) {
		// TODO Auto-generated method stub
		return null;
	}

	public static PacketImageData loadPacketImageData(BufferedImage bufferedImage) {
		// TODO Auto-generated method stub
		return null;
	}

	public static PacketImageData loadPacketImageData(InputStream inputStream) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public static PacketImageData loadPacketImageData(File imageFile) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
