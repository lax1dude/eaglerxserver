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

package net.lax1dude.eaglercraft.backend.server.base.message;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import net.lax1dude.eaglercraft.backend.server.base.pipeline.BufferUtils;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;

public class ByteBufOutputWrapper implements GamePacketOutputBuffer {

	public ByteBuf buffer;

	public ByteBufOutputWrapper() {
	}

	public ByteBufOutputWrapper(ByteBuf buffer) {
		this.buffer = buffer;
	}

	@Override
	public void write(int b) throws IOException {
		buffer.writeByte(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		buffer.writeBytes(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		buffer.writeBytes(b, off, len);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException {
		buffer.writeBoolean(v);
	}

	@Override
	public void writeByte(int v) throws IOException {
		buffer.writeByte(v);
	}

	@Override
	public void writeShort(int v) throws IOException {
		buffer.writeShort(v);
	}

	@Override
	public void writeChar(int v) throws IOException {
		buffer.writeChar(v);
	}

	@Override
	public void writeInt(int v) throws IOException {
		buffer.writeInt(v);
	}

	@Override
	public void writeLong(long v) throws IOException {
		buffer.writeLong(v);
	}

	@Override
	public void writeFloat(float v) throws IOException {
		buffer.writeFloat(v);
	}

	@Override
	public void writeDouble(double v) throws IOException {
		buffer.writeDouble(v);
	}

	@Override
	public void writeBytes(String s) throws IOException {
		int l = s.length();
		for(int i = 0; i < l; ++i) {
			buffer.writeByte((int)s.charAt(i));
		}
	}

	@Override
	public void writeChars(String s) throws IOException {
		int l = s.length();
		for(int i = 0; i < l; ++i) {
			buffer.writeChar(s.charAt(i));
		}
	}

	@Override
	public void writeUTF(String str) throws IOException {
		long utfCount = countUTFBytes(str);
		if (utfCount > 65535) {
			throw new IOException("String is longer than 65535 bytes when encoded as UTF8!");
		}
		byte[] arr = new byte[(int) utfCount + 2];
		int offset = 2;
		arr[0] = (byte)(((int)utfCount >>> 8) & 0xFF);
		arr[1] = (byte)((int)utfCount & 0xFF);
		offset = writeUTFBytesToBuffer(str, arr, offset);
		buffer.writeBytes(arr, 0, offset);
	}

	private static long countUTFBytes(String str) {
		int utfCount = 0;
		int length = str.length();
		for (int i = 0; i < length; i++) {
			int charValue = str.charAt(i);
			if (charValue > 0 && charValue <= 127) {
				utfCount++;
			} else if (charValue <= 2047) {
				utfCount += 2;
			} else {
				utfCount += 3;
			}
		}
		return utfCount;
	}

	private static int writeUTFBytesToBuffer(String str, byte[] buffer, int offset) throws IOException {
		int length = str.length();
		for (int i = 0; i < length; i++) {
			int charValue = str.charAt(i);
			if (charValue > 0 && charValue <= 127) {
				buffer[offset++] = (byte) charValue;
			} else if (charValue <= 2047) {
				buffer[offset++] = (byte) (0xc0 | (0x1f & (charValue >> 6)));
				buffer[offset++] = (byte) (0x80 | (0x3f & charValue));
			} else {
				buffer[offset++] = (byte) (0xe0 | (0x0f & (charValue >> 12)));
				buffer[offset++] = (byte) (0x80 | (0x3f & (charValue >> 6)));
				buffer[offset++] = (byte) (0x80 | (0x3f & charValue));
			}
		}
		return offset;
	}

	@Override
	public void writeVarInt(int i) throws IOException {
		BufferUtils.writeVarInt(buffer, i);
	}

	@Override
	public void writeVarLong(long i) throws IOException {
		BufferUtils.writeVarLong(buffer, i);
	}

	@Override
	public void writeStringMC(String str) throws IOException {
		byte[] abyte = str.getBytes(StandardCharsets.UTF_8);
		if (abyte.length > 32767) {
			throw new IOException("String too big (was " + str.length() + " bytes encoded, max " + 32767 + ")");
		} else {
			BufferUtils.writeVarInt(buffer, abyte.length);
			buffer.writeBytes(abyte);
		}
	}

	@Override
	public void writeStringEaglerASCII8(String str) throws IOException {
		int len = str.length();
		if(len > 255) {
			throw new IOException("String is longer than 255 chars! (" + len + ")");
		}
		buffer.writeByte(len);
		for(int i = 0, j; i < len; ++i) {
			j = (int)str.charAt(i);
			if(j > 255) {
				j = (int)'?';
			}
			buffer.writeByte(j);
		}
	}

	@Override
	public void writeStringEaglerASCII16(String str) throws IOException {
		int len = str.length();
		if(len > 65535) {
			throw new IOException("String is longer than 65535 chars! (" + len + ")");
		}
		buffer.writeShort(len);
		for(int i = 0, j; i < len; ++i) {
			j = (int)str.charAt(i);
			if(j > 255) {
				j = (int)'?';
			}
			buffer.writeByte(j);
		}
	}

	@Override
	public void writeByteArrayMC(byte[] bytes) throws IOException {
		BufferUtils.writeVarInt(buffer, bytes.length);
		buffer.writeBytes(bytes);
	}

	@Override
	public OutputStream stream() {
		return new ByteBufOutputStream(buffer);
	}

}
