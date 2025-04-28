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
import java.io.OutputStream;
import java.util.Arrays;

public class ReusableByteArrayOutputStream extends OutputStream {

	private byte[] currentBuffer = null;
	private int idx = 0;
	private int originalSize = 0;

	public void feedBuffer(byte[] buf) {
		currentBuffer = buf;
		idx = 0;
		originalSize = buf == null ? 0 : buf.length;
	}

	public boolean hasGrown() {
		return currentBuffer.length != originalSize;
	}

	public byte[] returnBuffer() {
		return currentBuffer.length == idx ? currentBuffer : Arrays.copyOf(currentBuffer, idx);
	}

	public byte[] returnBufferCopied() {
		return (currentBuffer.length == idx && currentBuffer.length != originalSize) ? currentBuffer
				: Arrays.copyOf(currentBuffer, idx);
	}

	private void growBuffer(int i) {
		int ii = currentBuffer.length;
		int iii = i - ii;
		if (iii > 0) {
			int j = ii + (ii >> 1);
			while (j < i) {
				j += (j >> 1);
			}
			byte[] n = new byte[j];
			System.arraycopy(currentBuffer, 0, n, 0, ii);
			currentBuffer = n;
		}
	}

	public int getWriterIndex() {
		return idx;
	}

	public void setWriterIndex(int i) {
		idx = i;
	}

	@Override
	public void write(int b) throws IOException {
		if (idx >= currentBuffer.length) {
			growBuffer(idx + 1);
		}
		currentBuffer[idx++] = (byte) b;
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		if (idx + len > currentBuffer.length) {
			growBuffer(idx + len);
		}
		System.arraycopy(b, off, currentBuffer, idx, len);
		idx += len;
	}

}
