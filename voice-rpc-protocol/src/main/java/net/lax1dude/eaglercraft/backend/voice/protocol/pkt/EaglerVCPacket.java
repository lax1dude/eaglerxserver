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

package net.lax1dude.eaglercraft.backend.voice.protocol.pkt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface EaglerVCPacket {

	void readPacket(DataInput buffer) throws IOException;

	void writePacket(DataOutput buffer) throws IOException;

	void handlePacket(EaglerVCHandler handler);

	int length();

	public static void writeString(DataOutput buffer, String str, boolean len16, Charset charset) throws IOException {
		if (str == null || str.length() == 0) {
			if (len16) {
				buffer.writeShort(0);
			} else {
				buffer.writeByte(0);
			}
			return;
		}
		byte[] bytes = str.getBytes(charset);
		if (bytes.length > (len16 ? 65535 : 255)) {
			throw new IOException("String is too long!");
		}
		if (len16) {
			buffer.writeShort(bytes.length);
		} else {
			buffer.writeByte(bytes.length);
		}
		buffer.write(bytes);
	}

	public static String readString(DataInput buffer, int maxLen, boolean len16, Charset charset) throws IOException {
		int len = len16 ? buffer.readUnsignedShort() : buffer.readUnsignedByte();
		if (len > maxLen) {
			throw new IOException("String is too long!");
		}
		if (len == 0) {
			return "";
		}
		byte[] toRead = new byte[len];
		buffer.readFully(toRead);
		String ret = new String(toRead, charset);
		if (charset != StandardCharsets.US_ASCII && ret.length() > maxLen) {
			throw new IOException("String is too long!");
		}
		return ret;
	}

}
