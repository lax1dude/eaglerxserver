package net.lax1dude.eaglercraft.backend.rpc.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;

public interface IPacketImageLoader {

	@Nonnull
	IPacketImageData loadPacketImageData(@Nonnull int[] pixelsARGB8, int width, int height);

	@Nonnull
	default IPacketImageData loadPacketImageData(@Nonnull BufferedImage bufferedImage) {
		return loadPacketImageData(bufferedImage, 255, 255);
	}

	@Nonnull
	IPacketImageData loadPacketImageData(@Nonnull BufferedImage bufferedImage, int maxWidth, int maxHeight);

	@Nonnull
	default IPacketImageData loadPacketImageData(@Nonnull @WillNotClose InputStream inputStream) throws IOException {
		return loadPacketImageData(inputStream, 255, 255);
	}

	@Nonnull
	IPacketImageData loadPacketImageData(@Nonnull @WillNotClose InputStream inputStream, int maxWidth, int maxHeight) throws IOException;

	@Nonnull
	default IPacketImageData loadPacketImageData(@Nonnull File imageFile) throws IOException {
		return loadPacketImageData(imageFile, 255, 255);
	}

	@Nonnull
	IPacketImageData loadPacketImageData(@Nonnull File imageFile, int maxWidth, int maxHeight) throws IOException;

}
