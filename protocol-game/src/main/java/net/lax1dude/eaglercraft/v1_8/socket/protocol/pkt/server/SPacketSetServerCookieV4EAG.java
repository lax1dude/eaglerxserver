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

package net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server;

import java.io.IOException;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public class SPacketSetServerCookieV4EAG implements GameMessagePacket {

	public boolean revokeQuerySupported;
	public boolean saveCookieToDisk;
	public long expires;
	public byte[] data;

	public SPacketSetServerCookieV4EAG() {
	}

	public SPacketSetServerCookieV4EAG(byte[] data, long expires, boolean revokeQuerySupported,
			boolean saveCookieToDisk) {
		if (data.length > 255) {
			throw new IllegalArgumentException("Cookie is too large! (Max 255 bytes)");
		}
		this.data = data;
		this.expires = expires;
		this.revokeQuerySupported = revokeQuerySupported;
		this.saveCookieToDisk = saveCookieToDisk;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		byte b = buffer.readByte();
		revokeQuerySupported = (b & 1) != 0;
		saveCookieToDisk = (b & 2) != 0;
		expires = buffer.readVarLong();
		int len = buffer.readUnsignedByte();
		if (len > 0) {
			data = new byte[len];
			buffer.readFully(data);
		} else {
			data = null;
		}
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		if (data != null && data.length > 255) {
			throw new IOException("Cookie is too large! (Max 255 bytes)");
		}
		byte b = 0;
		if (revokeQuerySupported)
			b |= 1;
		if (saveCookieToDisk)
			b |= 2;
		buffer.writeByte(b);
		buffer.writeVarLong(expires);
		if (data != null) {
			buffer.writeByte(data.length);
			buffer.write(data);
		} else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return GamePacketOutputBuffer.getVarLongSize(expires) + 2 + data.length;
	}

}
