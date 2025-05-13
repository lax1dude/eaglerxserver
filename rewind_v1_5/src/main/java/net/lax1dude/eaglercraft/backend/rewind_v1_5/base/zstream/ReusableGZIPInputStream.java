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
import java.util.zip.CRC32;
import java.util.zip.Inflater;
import java.util.zip.ZipException;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.base.codec.BufferUtils;

/**
 * Note: Based on OpenJDK
 */
public class ReusableGZIPInputStream extends ReusableInflaterInputStream {

	protected CRC32 crc = new CRC32();

	protected boolean eos;

	private boolean closed = false;

	private int start = 0;
	private int remaining = -1;

	private void ensureOpen() throws IOException {
		if (closed) {
			throw new IOException("Stream closed");
		}
	}

	public ReusableGZIPInputStream(Inflater inf, byte[] singleByteBuf) {
		super(inf, singleByteBuf);
	}

	public void setInput(ByteBuf dataIn, int limit) throws IOException {
		closed = false;
		eos = false;
		if (BufferUtils.LITTLE_ENDIAN_SUPPORT) {
			readHeaderLE(dataIn);
		} else {
			readHeader(dataIn);
		}
		start = dataIn.readerIndex();
		remaining = limit;
		super.setInput(dataIn);
	}

	public int read(byte[] buf, int off, int len) throws IOException {
		ensureOpen();
		if (eos) {
			return -1;
		}
		int n = super.read(buf, off, len);
		if (n == -1) {
			if (BufferUtils.LITTLE_ENDIAN_SUPPORT) {
				readTrailerLE();
			} else {
				readTrailer();
			}
			eos = true;
		} else {
			if (remaining >= 0) {
				if (n > remaining) {
					throw new IOException("Too many bytes decompressed!");
				} else {
					remaining -= n;
				}
			}
			crc.update(buf, off, n);
		}
		return n;
	}

	public void close() throws IOException {
		if (!closed) {
			if (inf.finished()) {
				if (BufferUtils.LITTLE_ENDIAN_SUPPORT) {
					readTrailerLE();
				} else {
					readTrailer();
				}
				eos = true;
			}
			buf.readerIndex(start + inf.getTotalIn());
			super.close();
			eos = true;
			closed = true;
		}
	}

	public static final int GZIP_MAGIC = 0x8b1f;

	/*
	 * File header flags.
	 */
	private static final int FTEXT = 1; // Extra text
	private static final int FHCRC = 2; // Header CRC
	private static final int FEXTRA = 4; // Extra field
	private static final int FNAME = 8; // File name
	private static final int FCOMMENT = 16; // File comment

	private int readHeaderLE(ByteBuf dataIn) throws IOException {
		int start = dataIn.readerIndex();
		// Check header magic
		if (dataIn.readUnsignedShortLE() != GZIP_MAGIC) {
			throw new ZipException("Not in GZIP format");
		}
		// Check compression method
		if (dataIn.readUnsignedByte() != 8) {
			throw new ZipException("Unsupported compression method");
		}
		// Read flags
		int flg = dataIn.readUnsignedByte();
		// Skip MTIME, XFL, and OS fields
		dataIn.skipBytes(6);
		int n = 2 + 2 + 6;
		// Skip optional extra field
		if ((flg & FEXTRA) == FEXTRA) {
			int m = dataIn.readUnsignedShortLE();
			dataIn.skipBytes(m);
			n += m + 2;
		}
		// Skip optional file name
		if ((flg & FNAME) == FNAME) {
			do {
				n++;
			} while (dataIn.readUnsignedByte() != 0);
		}
		// Skip optional file comment
		if ((flg & FCOMMENT) == FCOMMENT) {
			do {
				n++;
			} while (dataIn.readUnsignedByte() != 0);
		}
		// Check optional header CRC
		if ((flg & FHCRC) == FHCRC) {
			int v = (int) getByteBufCRC(dataIn, start, dataIn.readerIndex() - start) & 0xffff;
			if (dataIn.readUnsignedShortLE() != v) {
				throw new ZipException("Corrupt GZIP header");
			}
			n += 2;
		}
		crc.reset();
		return n;
	}

	private int readHeader(ByteBuf dataIn) throws IOException {
		int start = dataIn.readerIndex();
		// Check header magic
		if ((dataIn.readUnsignedByte() | (dataIn.readUnsignedByte() << 8)) != GZIP_MAGIC) {
			throw new ZipException("Not in GZIP format");
		}
		// Check compression method
		if (dataIn.readUnsignedByte() != 8) {
			throw new ZipException("Unsupported compression method");
		}
		// Read flags
		int flg = dataIn.readUnsignedByte();
		// Skip MTIME, XFL, and OS fields
		dataIn.skipBytes(6);
		int n = 2 + 2 + 6;
		// Skip optional extra field
		if ((flg & FEXTRA) == FEXTRA) {
			int m = (dataIn.readUnsignedByte() | (dataIn.readUnsignedByte() << 8));
			dataIn.skipBytes(m);
			n += m + 2;
		}
		// Skip optional file name
		if ((flg & FNAME) == FNAME) {
			do {
				n++;
			} while (dataIn.readUnsignedByte() != 0);
		}
		// Skip optional file comment
		if ((flg & FCOMMENT) == FCOMMENT) {
			do {
				n++;
			} while (dataIn.readUnsignedByte() != 0);
		}
		// Check optional header CRC
		if ((flg & FHCRC) == FHCRC) {
			int v = (int) getByteBufCRC(dataIn, start, dataIn.readerIndex() - start) & 0xffff;
			if ((dataIn.readUnsignedByte() | (dataIn.readUnsignedByte() << 8)) != v) {
				throw new ZipException("Corrupt GZIP header");
			}
			n += 2;
		}
		crc.reset();
		return n;
	}

	private int getByteBufCRC(ByteBuf dataIn, int offset, int len) {
		crc.reset();
		if (dataIn.hasArray()) {
			byte[] arr = dataIn.array();
			int arrIndex = dataIn.arrayOffset();
			crc.update(arr, arrIndex + offset, len);
		} else if (dataIn.nioBufferCount() == 1) {
			crc.update(dataIn.internalNioBuffer(offset, len));
		} else {
			throw new IllegalStateException("Composite buffers not supported! (Input)");
		}
		return (int) crc.getValue();
	}

	/*
	 * Reads GZIP member trailer and returns true if the eos reached, false if there
	 * are more (concatenated gzip data set)
	 */
	private boolean readTrailerLE() throws IOException {
		ByteBuf in = this.buf;
		in.readerIndex(start + inf.getTotalIn());
		// Uses left-to-right evaluation order
		if ((in.readUnsignedIntLE() != crc.getValue()) ||
		// rfc1952; ISIZE is the input size modulo 2^32
				(in.readUnsignedIntLE() != (inf.getBytesWritten() & 0xffffffffL)))
			throw new ZipException("Corrupt GZIP trailer");
		start += 8;
		return true;
	}

	private boolean readTrailer() throws IOException {
		ByteBuf in = this.buf;
		in.readerIndex(start + inf.getTotalIn());
		// Uses left-to-right evaluation order
		if (((in.readUnsignedByte() | (in.readUnsignedByte() << 8) | (in.readUnsignedByte() << 16)
				| (in.readUnsignedByte() << 24)) != crc.getValue()) ||
		// rfc1952; ISIZE is the input size modulo 2^32
				((in.readUnsignedByte() | (in.readUnsignedByte() << 8) | (in.readUnsignedByte() << 16)
						| (in.readUnsignedByte() << 24)) != (inf.getBytesWritten() & 0xffffffffL)))
			throw new ZipException("Corrupt GZIP trailer");
		start += 8;
		return true;
	}

}
