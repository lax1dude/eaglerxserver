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
import java.io.OutputStream;
import java.util.zip.Deflater;

import io.netty.buffer.ByteBuf;

/**
 * Note: Based on OpenJDK
 */
public class ReusableDeflaterOutputStream extends OutputStream {

	// The amount to write to the output every cycle
	private static final int BLOCK_SIZE = 512;

	protected Deflater def;
	protected ByteBuf buf;
	protected boolean array;
	private boolean closed = false;
	private byte[] singleByteBuf;

	public ReusableDeflaterOutputStream(Deflater def, byte[] singleByteBuf) {
		this.def = def;
		this.singleByteBuf = singleByteBuf;
	}

	public void setOutput(ByteBuf buf) {
		def.reset();
		this.closed = false;
		this.array = buf.hasArray();
		if(!array && buf.nioBufferCount() != 1) {
			throw new IllegalStateException("Composite buffers not supported! (Output)");
		}
		this.buf = buf;
	}

	public void write(int b) throws IOException {
		singleByteBuf[0] = (byte) (b & 0xff);
		write(singleByteBuf, 0, 1);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		if (def.finished()) {
			throw new IOException("write beyond end of stream");
		}
		if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		if (!def.finished()) {
			def.setInput(b, off, len);
			while (!def.needsInput()) {
				deflate();
			}
		}
	}

	public void finish() throws IOException {
		if (!def.finished()) {
			def.finish();
			while (!def.finished()) {
				deflate();
			}
		}
	}

	public void close() throws IOException {
		if (!closed) {
			finish();
			closed = true;
		}
	}

	protected void deflate() throws IOException {
		int writable = buf.writableBytes();
		if(writable < BLOCK_SIZE) {
			writable = BLOCK_SIZE;
		}
		buf.ensureWritable(writable);
		int len;
		int writerIndex = buf.writerIndex();
		if(array) {
			len = def.deflate(buf.array(), buf.arrayOffset() + writerIndex, writable);
		}else {
			len = def.deflate(buf.internalNioBuffer(writerIndex, writable));
		}
		if (len > 0) {
			buf.writerIndex(writerIndex + len);
		}
	}

}
