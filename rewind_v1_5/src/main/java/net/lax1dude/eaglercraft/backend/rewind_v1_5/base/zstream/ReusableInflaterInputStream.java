/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.rewind_v1_5.base.zstream;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;

import io.netty.buffer.ByteBuf;

/**
 * Note: Based on OpenJDK
 */
public class ReusableInflaterInputStream extends InputStream {

	protected ByteBuf buf;
	protected Inflater inf;
	protected int len;
	private boolean closed = false;
	private boolean reachEOF = false;
	private byte[] singleByteBuf;

	public ReusableInflaterInputStream(Inflater inf, byte[] singleByteBuf) {
		this.inf = inf;
		this.singleByteBuf = singleByteBuf;
	}

	public void setInput(ByteBuf dataIn) throws IOException {
		closed = false;
		reachEOF = false;
		inf.reset();
		if (dataIn.hasArray()) {
			byte[] arr = dataIn.array();
			int arrIndex = dataIn.arrayOffset();
			inf.setInput(arr, arrIndex + dataIn.readerIndex(), dataIn.readableBytes());
			buf = dataIn;
		} else if (dataIn.nioBufferCount() == 1) {
			inf.setInput(dataIn.internalNioBuffer(dataIn.readerIndex(), dataIn.readableBytes()));
			buf = dataIn;
		} else {
			throw new IllegalStateException("Composite buffers not supported! (Input)");
		}
	}

	private void ensureOpen() throws IOException {
		if (closed) {
			throw new IOException("Stream closed");
		}
	}

	public int read() throws IOException {
		return read(singleByteBuf, 0, 1) == -1 ? -1 : Byte.toUnsignedInt(singleByteBuf[0]);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		ensureOpen();
		if (b == null) {
			throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}
		try {
			int n;
			if ((n = inf.inflate(b, off, len)) == 0) {
				if (inf.finished() || inf.needsDictionary()) {
					reachEOF = true;
					return -1;
				}
				if (inf.needsInput()) {
					throw new ZipException("Input data was not complete");
				}
			}
			return n;
		} catch (DataFormatException e) {
			String s = e.getMessage();
			throw new ZipException(s != null ? s : "Invalid ZLIB data format");
		}
	}

	public int available() throws IOException {
		ensureOpen();
		if (reachEOF) {
			return 0;
		} else if (inf.finished()) {
			// the end of the compressed data stream has been reached
			reachEOF = true;
			return 0;
		} else {
			return 1;
		}
	}

	private byte[] b = new byte[512];

	public long skip(long n) throws IOException {
		if (n < 0) {
			throw new IllegalArgumentException("negative skip length");
		}
		ensureOpen();
		int max = (int) Math.min(n, Integer.MAX_VALUE);
		int total = 0;
		while (total < max) {
			int len = max - total;
			if (len > b.length) {
				len = b.length;
			}
			len = read(b, 0, len);
			if (len == -1) {
				reachEOF = true;
				break;
			}
			total += len;
		}
		return total;
	}

	public void close() throws IOException {
		closed = true;
		buf = null;
	}

	public boolean markSupported() {
		return false;
	}

	public void mark(int readlimit) {
	}

	public void reset() throws IOException {
		throw new IOException("mark/reset not supported");
	}

}
