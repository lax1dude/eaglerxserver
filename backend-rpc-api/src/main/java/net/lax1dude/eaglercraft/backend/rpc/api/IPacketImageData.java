package net.lax1dude.eaglercraft.backend.rpc.api;

public interface IPacketImageData {

	int getWidth();

	int getHeight();

	void getPixels(int[] dest, int offset);

}
