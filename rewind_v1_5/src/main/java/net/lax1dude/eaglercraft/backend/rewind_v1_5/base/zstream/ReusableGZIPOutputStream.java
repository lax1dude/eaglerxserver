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
import java.util.zip.Deflater;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.rewind_v1_5.base.codec.BufferUtils;

/**
 * Note: Based on OpenJDK
 */
public class ReusableGZIPOutputStream extends ReusableDeflaterOutputStream {

	protected CRC32 crc = new CRC32();

	private static final int GZIP_MAGIC = 0x8b1f;

	// Represents the default "unknown" value for OS header, per RFC-1952
	private static final byte OS_UNKNOWN = (byte) 255;

	public ReusableGZIPOutputStream(Deflater inf, byte[] singleByteBuf) {
		super(inf, singleByteBuf);
	}

	public void setOutput(ByteBuf buf) {
		super.setOutput(buf);
		crc.reset();
		writeHeader();
	}

	public void write(byte[] buf, int off, int len) throws IOException {
		super.write(buf, off, len);
		crc.update(buf, off, len);
	}

	public void finish() throws IOException {
		if (!def.finished()) {
			super.finish();
			if(BufferUtils.LITTLE_ENDIAN_SUPPORT) {
				writeTrailerLE();
			}else {
				writeTrailer();
			}
		}
	}

	/*
	 * Writes GZIP member header.
	 */
	private void writeHeader() {
		this.buf.writeBytes(new byte[] { (byte) GZIP_MAGIC, // Magic number (short)
				(byte) (GZIP_MAGIC >> 8), // Magic number (short)
				Deflater.DEFLATED, // Compression method (CM)
				0, // Flags (FLG)
				0, // Modification time MTIME (int)
				0, // Modification time MTIME (int)
				0, // Modification time MTIME (int)
				0, // Modification time MTIME (int)
				0, // Extra flags (XFLG)
				OS_UNKNOWN // Operating system (OS)
		});
	}

	/*
	 * Writes GZIP member trailer to a byte array, starting at a given offset.
	 */
	private void writeTrailerLE() {
		this.buf.writeIntLE((int) crc.getValue()); // CRC-32 of uncompr. data
		this.buf.writeIntLE(def.getTotalIn()); // Number of uncompr. bytes
	}

	private void writeTrailer() {
		int i = (int) crc.getValue(); // CRC-32 of uncompr. data
		this.buf.writeByte(i);
		this.buf.writeByte(i >>> 8);
		this.buf.writeByte(i >>> 16);
		this.buf.writeByte(i >>> 24);
		i = def.getTotalIn(); // Number of uncompr. bytes
		this.buf.writeByte(i);
		this.buf.writeByte(i >>> 8);
		this.buf.writeByte(i >>> 16);
		this.buf.writeByte(i >>> 24);
	}

}
