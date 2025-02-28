package net.lax1dude.eaglercraft.backend.server.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public interface IPacketImageLoader {

	PacketImageData loadPacketImageData(int[] pixelsARGB8, int width, int height);

	default PacketImageData loadPacketImageData(BufferedImage bufferedImage) {
		return loadPacketImageData(bufferedImage, 255, 255);
	}

	PacketImageData loadPacketImageData(BufferedImage bufferedImage, int maxWidth, int maxHeight);

	default PacketImageData loadPacketImageData(InputStream inputStream) throws IOException {
		return loadPacketImageData(inputStream, 255, 255);
	}

	PacketImageData loadPacketImageData(InputStream inputStream, int maxWidth, int maxHeight) throws IOException;

	default PacketImageData loadPacketImageData(File imageFile) throws IOException {
		return loadPacketImageData(imageFile, 255, 255);
	}

	PacketImageData loadPacketImageData(File imageFile, int maxWidth, int maxHeight) throws IOException;

}
