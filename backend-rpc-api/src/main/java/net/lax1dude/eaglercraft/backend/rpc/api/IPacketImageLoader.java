package net.lax1dude.eaglercraft.backend.rpc.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface IPacketImageLoader {

	IPacketImageData loadPacketImageData(int[] pixelsARGB8, int width, int height);

	default IPacketImageData loadPacketImageData(BufferedImage bufferedImage) {
		return loadPacketImageData(bufferedImage, 255, 255);
	}

	IPacketImageData loadPacketImageData(BufferedImage bufferedImage, int maxWidth, int maxHeight);

	default IPacketImageData loadPacketImageData(InputStream inputStream) throws IOException {
		return loadPacketImageData(inputStream, 255, 255);
	}

	IPacketImageData loadPacketImageData(InputStream inputStream, int maxWidth, int maxHeight) throws IOException;

	default IPacketImageData loadPacketImageData(File imageFile) throws IOException {
		return loadPacketImageData(imageFile, 255, 255);
	}

	IPacketImageData loadPacketImageData(File imageFile, int maxWidth, int maxHeight) throws IOException;

}
