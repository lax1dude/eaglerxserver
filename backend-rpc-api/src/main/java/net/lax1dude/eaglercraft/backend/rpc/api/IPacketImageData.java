package net.lax1dude.eaglercraft.backend.rpc.api;

public interface IPacketImageData {

	int getWidth();

	int getHeight();

	default int[] getPixels() {
		int[] arr = new int[getWidth() * getHeight()];
		getPixels(arr, 0);
		return arr;
	}

	default void getPixels(int[] dest) {
		getPixels(dest, 0);
	}

	void getPixels(int[] dest, int offset);

}
