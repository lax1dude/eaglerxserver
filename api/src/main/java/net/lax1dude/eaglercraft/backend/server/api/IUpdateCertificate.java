package net.lax1dude.eaglercraft.backend.server.api;

public interface IUpdateCertificate {

	int getLength();

	default void getBytes(byte[] dst) {
		getBytes(dst, 0);
	}

	default void getBytes(byte[] dst, int dstOffset) {
		getBytes(0, dst, dstOffset, getLength());
	}

	void getBytes(int srcOffset, byte[] dst, int dstOffset, int length);

	default byte[] toByteArray() {
		byte[] ret = new byte[getLength()];
		getBytes(ret, 0);
		return ret;
	}

}
