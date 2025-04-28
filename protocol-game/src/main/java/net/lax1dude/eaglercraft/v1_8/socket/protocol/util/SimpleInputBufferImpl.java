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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketInputBuffer;

public class SimpleInputBufferImpl extends DataInputStream implements GamePacketInputBuffer {

	protected byte[] toByteArrayReturns;
	protected int toByteArrayLength = -1;

	public SimpleInputBufferImpl(InputStream in) {
		super(in);
		this.toByteArrayReturns = null;
		this.toByteArrayLength = -1;
	}

	public SimpleInputBufferImpl(InputStream in, byte[] toByteArrayReturns) {
		super(in);
		this.toByteArrayReturns = toByteArrayReturns;
		this.toByteArrayLength = -1;
	}

	public SimpleInputBufferImpl(InputStream in, int toByteArrayLength) {
		super(in);
		this.toByteArrayReturns = null;
		this.toByteArrayLength = toByteArrayLength;
	}

	public void setStream(InputStream parent) {
		in = parent;
		toByteArrayReturns = null;
		toByteArrayLength = -1;
	}

	public void setToByteArrayReturns(byte[] toByteArrayReturns) {
		this.toByteArrayReturns = toByteArrayReturns;
		this.toByteArrayLength = -1;
	}

	public void setToByteArrayReturns(int len) {
		this.toByteArrayReturns = null;
		this.toByteArrayLength = len;
	}

	@Override
	public void skipAllBytes(int n) throws IOException {
		if (skipBytes(n) != n) {
			throw new EOFException();
		}
	}

	@Override
	public int readVarInt() throws IOException {
		int i = 0;
		int j = 0;

		while (true) {
			int b0 = in.read();
			if (b0 < 0) {
				throw new EOFException();
			}
			i |= (b0 & 127) << j++ * 7;
			if (j > 5) {
				throw new IOException("VarInt too big");
			}

			if ((b0 & 128) != 128) {
				break;
			}
		}

		return i;
	}

	@Override
	public long readVarLong() throws IOException {
		long i = 0L;
		int j = 0;

		while (true) {
			int b0 = in.read();
			if (b0 < 0) {
				throw new EOFException();
			}
			i |= (long) (b0 & 127) << j++ * 7;
			if (j > 10) {
				throw new IOException("VarLong too big");
			}

			if ((b0 & 128) != 128) {
				break;
			}
		}

		return i;
	}

	@Override
	public String readStringMC(int maxLen) throws IOException {
		int i = this.readVarInt();
		if (i > (maxLen << 2)) {
			throw new IOException("The received encoded string buffer length is longer than maximum allowed (" + i
					+ " > " + (maxLen << 2) + ")");
		} else if (i < 0) {
			throw new IOException("The received encoded string buffer length is less than zero! Weird string!");
		} else {
			byte[] toRead = new byte[i];
			this.readFully(toRead);
			String s = new String(toRead, StandardCharsets.UTF_8);
			if (s.length() > maxLen) {
				throw new IOException(
						"The received string length is longer than maximum allowed (" + i + " > " + maxLen + ")");
			} else {
				return s;
			}
		}
	}

	@Override
	public String readStringEaglerASCII8() throws IOException {
		int len = in.read();
		if (len < 0) {
			throw new EOFException();
		}
		char[] ret = new char[len];
		for (int i = 0, j; i < len; ++i) {
			j = in.read();
			if (j < 0) {
				throw new EOFException();
			}
			ret[i] = (char) j;
		}
		return new String(ret);
	}

	@Override
	public String readStringEaglerASCII16() throws IOException {
		int len = readUnsignedShort();
		char[] ret = new char[len];
		for (int i = 0, j; i < len; ++i) {
			j = in.read();
			if (j < 0) {
				throw new EOFException();
			}
			ret[i] = (char) j;
		}
		return new String(ret);
	}

	@Override
	public byte[] readByteArrayMC() throws IOException {
		byte[] abyte = new byte[this.readVarInt()];
		this.readFully(abyte);
		return abyte;
	}

	@Override
	public InputStream stream() {
		return in;
	}

	@Override
	public byte[] toByteArray() throws IOException {
		if (toByteArrayReturns != null) {
			return toByteArrayReturns;
		} else if (toByteArrayLength != -1) {
			byte[] ret = new byte[toByteArrayLength];
			in.read(ret);
			return ret;
		} else if (in instanceof ByteArrayInputStream) {
			ByteArrayInputStream bis = (ByteArrayInputStream) in;
			byte[] ret = new byte[bis.available()];
			bis.read(ret);
			return ret;
		} else {
			ByteArrayOutputStream bao = null;
			byte[] copyBuffer = new byte[in.available()];
			int i = in.read(copyBuffer);
			if (i == copyBuffer.length) {
				int j = in.read();
				if (j == -1) {
					return copyBuffer;
				} else {
					int k = Math.max(copyBuffer.length, 64);
					bao = new ByteArrayOutputStream(k + 1);
					bao.write(copyBuffer);
					bao.write(j);
					if (k != copyBuffer.length) {
						copyBuffer = new byte[k];
					}
				}
			} else {
				int j = Math.max(copyBuffer.length, 64);
				bao = new ByteArrayOutputStream(j);
				bao.write(copyBuffer);
				if (j != copyBuffer.length) {
					copyBuffer = new byte[j];
				}
			}
			while ((i = in.read(copyBuffer)) != -1) {
				bao.write(copyBuffer, 0, i);
			}
			return bao.toByteArray();
		}
	}

}
