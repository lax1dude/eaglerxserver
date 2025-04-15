package net.lax1dude.eaglercraft.backend.server.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public interface IPacketImageLoader {

	@Nonnull
	PacketImageData loadPacketImageData(@Nonnull int[] pixelsARGB8, int width, int height);

	@Nonnull
	default PacketImageData loadPacketImageData(@Nonnull BufferedImage bufferedImage) {
		return loadPacketImageData(bufferedImage, 255, 255);
	}

	@Nonnull
	PacketImageData loadPacketImageData(@Nonnull BufferedImage bufferedImage, int maxWidth, int maxHeight);

	@Nonnull
	default PacketImageData loadPacketImageData(@Nonnull InputStream inputStream) throws IOException {
		return loadPacketImageData(inputStream, 255, 255);
	}

	@Nonnull
	PacketImageData loadPacketImageData(@Nonnull InputStream inputStream, int maxWidth, int maxHeight) throws IOException;

	@Nonnull
	default PacketImageData loadPacketImageData(@Nonnull File imageFile) throws IOException {
		return loadPacketImageData(imageFile, 255, 255);
	}

	@Nonnull
	PacketImageData loadPacketImageData(@Nonnull File imageFile, int maxWidth, int maxHeight) throws IOException;

}
