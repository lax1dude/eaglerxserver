package net.lax1dude.eaglercraft.backend.server.api;

import javax.annotation.Nonnull;

public interface IUpdateCertificate {

	int getLength();

	default void getBytes(@Nonnull byte[] dst) {
		getBytes(dst, 0);
	}

	default void getBytes(@Nonnull byte[] dst, int dstOffset) {
		getBytes(0, dst, dstOffset, getLength());
	}

	void getBytes(int srcOffset, @Nonnull byte[] dst, int dstOffset, int length);

	@Nonnull
	default byte[] toByteArray() {
		byte[] ret = new byte[getLength()];
		getBytes(ret, 0);
		return ret;
	}

}
