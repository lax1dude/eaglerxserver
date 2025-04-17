package net.lax1dude.eaglercraft.backend.rpc.api;

import javax.annotation.Nonnull;

public interface IPacketImageData {

	int getWidth();

	int getHeight();

	@Nonnull
	default int[] getPixels() {
		int[] arr = new int[getWidth() * getHeight()];
		getPixels(arr, 0);
		return arr;
	}

	default void getPixels(@Nonnull int[] dest) {
		getPixels(dest, 0);
	}

	void getPixels(@Nonnull int[] dest, int offset);

}
