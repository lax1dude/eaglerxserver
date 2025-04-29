/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.v1_8.socket.protocol.util;

import java.io.IOException;
import java.io.InputStream;

public class ReusableByteArrayInputStream extends InputStream {

	private byte[] currentBuffer = null;
	private int idx = 0;
	private int markIDX = 0;

	public void feedBuffer(byte[] b) {
		currentBuffer = b;
		idx = 0;
		markIDX = 0;
	}

	public void feedBuffer(byte[] b, int offset) {
		currentBuffer = b;
		idx = offset;
		markIDX = offset;
	}

	@Override
	public int read() throws IOException {
		if (currentBuffer.length <= idx)
			throw new IOException("ReusableByteArrayInputStream buffer underflow, no bytes remaining");
		return (int) currentBuffer[idx++] & 0xFF;
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
		if (idx + len > currentBuffer.length) {
			throw new IOException(
					"ReusableByteArrayInputStream buffer underflow, tried to read " + len + " when there are only "
							+ (currentBuffer.length - idx) + " bytes remaining",
					new ArrayIndexOutOfBoundsException(idx + len - 1));
		}
		if (off + len > b.length) {
			throw new ArrayIndexOutOfBoundsException(off + len - 1);
		}
		System.arraycopy(currentBuffer, idx, b, off, len);
		idx += len;
		return len;
	}

	public void mark() {
		markIDX = idx;
	}

	public void reset() {
		idx = markIDX;
	}

	public int getReaderIndex() {
		return idx;
	}

	public int available() {
		return Math.max(currentBuffer.length - idx, 0);
	}

	public void setReaderIndex(int i) {
		idx = i;
		markIDX = i;
	}

	public boolean markSupported() {
		return true;
	}

}
