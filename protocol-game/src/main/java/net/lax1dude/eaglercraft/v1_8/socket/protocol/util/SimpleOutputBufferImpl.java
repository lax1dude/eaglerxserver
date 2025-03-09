package net.lax1dude.eaglercraft.v1_8.socket.protocol.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;

/**
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
public class SimpleOutputBufferImpl extends DataOutputStream implements GamePacketOutputBuffer {

	public SimpleOutputBufferImpl(OutputStream out) {
		super(out);
	}

	public void setStream(OutputStream parent) {
		out = parent;
	}

	@Override
	public void writeVarInt(int i) throws IOException {
		while ((i & -128) != 0) {
			out.write(i & 127 | 128);
			i >>>= 7;
		}
		out.write(i);
	}

	@Override
	public void writeVarLong(long i) throws IOException {
		while ((i & -128L) != 0L) {
			out.write((int) (i & 127L) | 128);
			i >>>= 7;
		}
		out.write((int) i);
	}

	@Override
	public void writeStringMC(String str) throws IOException {
		byte[] abyte = str.getBytes(StandardCharsets.UTF_8);
		if (abyte.length > 32767) {
			throw new IOException("String too big (was " + str.length() + " bytes encoded, max " + 32767 + ")");
		} else {
			this.writeVarInt(abyte.length);
			this.write(abyte);
		}
	}

	@Override
	public void writeStringEaglerASCII8(String str) throws IOException {
		int len = str.length();
		if(len > 255) {
			throw new IOException("String is longer than 255 chars! (" + len + ")");
		}
		out.write(len);
		for(int i = 0, j; i < len; ++i) {
			j = (int)str.charAt(i);
			if(j > 255) {
				j = (int)'?';
			}
			out.write(j);
		}
	}

	@Override
	public void writeStringEaglerASCII16(String str) throws IOException {
		int len = str.length();
		if(len > 65535) {
			throw new IOException("String is longer than 65535 chars! (" + len + ")");
		}
		writeShort(len);
		for(int i = 0, j; i < len; ++i) {
			j = (int)str.charAt(i);
			if(j > 255) {
				j = (int)'?';
			}
			out.write(j);
		}
	}

	@Override
	public void writeByteArrayMC(byte[] bytes) throws IOException {
		this.writeVarInt(bytes.length);
		this.write(bytes);
	}

	@Override
	public OutputStream stream() {
		return out;
	}

}
