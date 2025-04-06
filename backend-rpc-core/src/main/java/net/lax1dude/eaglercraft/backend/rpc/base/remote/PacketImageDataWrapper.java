package net.lax1dude.eaglercraft.backend.rpc.base.remote;

import net.lax1dude.eaglercraft.backend.rpc.api.IPacketImageData;
import net.lax1dude.eaglercraft.backend.rpc.protocol.util.PacketImageData;

public final class PacketImageDataWrapper implements IPacketImageData {

	public static IPacketImageData wrap(PacketImageData image) {
		return new PacketImageDataWrapper(image);
	}

	public static PacketImageData unwrap(IPacketImageData image) {
		return ((PacketImageDataWrapper)image).image;
	}

	private final PacketImageData image;

	private PacketImageDataWrapper(PacketImageData image) {
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

	@Override
	public boolean equals(Object obj) {
		return this == obj || ((obj instanceof PacketImageDataWrapper) && image.equals(((PacketImageDataWrapper)obj).image));
	}

	@Override
	public int hashCode() {
		return image.hashCode();
	}

}
