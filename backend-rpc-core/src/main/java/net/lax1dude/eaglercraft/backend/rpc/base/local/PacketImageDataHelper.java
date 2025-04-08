package net.lax1dude.eaglercraft.backend.rpc.base.local;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;
import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

class PacketImageDataHelper implements IPacketImageLoader {

	static IPacketImageData wrap(PacketImageData image) {
		if(image == null) return null;
		return new PacketImageDataLocal(image);
	}

	static PacketImageData unwrap(IPacketImageData image) {
		if(image == null) return null;
		return ((PacketImageDataLocal)image).image;
	}

	static class PacketImageDataLocal implements IPacketImageData {

		final PacketImageData image;

		PacketImageDataLocal(PacketImageData image) {
			this.image = image;
		}

		@Override
		public int getWidth() {
			return image.width;
		}

		@Override
		public int getHeight() {
			return image.height;
		}

		@Override
		public void getPixels(int[] dest, int offset) {
			System.arraycopy(image.rgba, 0, dest, offset, image.rgba.length);
		}

	}

	private final net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader loader;

	PacketImageDataHelper(net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader loader) {
		this.loader = loader;
	}

	@Override
	public IPacketImageData loadPacketImageData(int[] pixelsARGB8, int width, int height) {
		return wrap(loader.loadPacketImageData(pixelsARGB8, width, height));
	}

	@Override
	public IPacketImageData loadPacketImageData(BufferedImage bufferedImage, int maxWidth, int maxHeight) {
		return wrap(loader.loadPacketImageData(bufferedImage, maxWidth, maxHeight));
	}

	@Override
	public IPacketImageData loadPacketImageData(InputStream inputStream, int maxWidth, int maxHeight)
			throws IOException {
		return wrap(loader.loadPacketImageData(inputStream, maxWidth, maxHeight));
	}

	@Override
	public IPacketImageData loadPacketImageData(File imageFile, int maxWidth, int maxHeight) throws IOException {
		return wrap(loader.loadPacketImageData(imageFile, maxWidth, maxHeight));
	}

}
