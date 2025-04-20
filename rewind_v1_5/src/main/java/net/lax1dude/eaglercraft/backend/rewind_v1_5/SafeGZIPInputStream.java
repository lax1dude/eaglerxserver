package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class SafeGZIPInputStream extends InputStream {
	private long bytesReadTotal = 0;
	private final long maxBytes;
	private final GZIPInputStream in;

	public SafeGZIPInputStream(GZIPInputStream in, long maxBytes) {
		this.in = in;
		this.maxBytes = maxBytes;
	}

	private void updateCount(int bytes) throws IOException {
		if (bytes > 0) {
			bytesReadTotal += bytes;
			if (bytesReadTotal > maxBytes) {
				throw new IOException("Decompressed data exceeds maximum allowed size (" + maxBytes + ")");
			}
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read = this.in.read(b, off, len);
		updateCount(read);
		return read;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int read = this.in.read(b);
		updateCount(read);
		return read;
	}

	@Override
	public int read() throws IOException {
		int read = this.in.read();
		updateCount(read == -1 ? -1 : 1);
		return read;
	}
}
