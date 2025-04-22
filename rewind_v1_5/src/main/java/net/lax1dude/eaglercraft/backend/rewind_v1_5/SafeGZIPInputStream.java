/*
 * Copyright (c) 2025 ayunami2000. All Rights Reserved.
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
