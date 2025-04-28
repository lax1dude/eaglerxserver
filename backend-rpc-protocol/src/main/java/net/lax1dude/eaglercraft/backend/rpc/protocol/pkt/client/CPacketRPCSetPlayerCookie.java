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

package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCSetPlayerCookie implements EaglerBackendRPCPacket {

	public boolean revokeQuerySupported;
	public boolean saveToDisk;
	public int expires;
	public byte[] cookieData;

	public CPacketRPCSetPlayerCookie() {
	}

	public CPacketRPCSetPlayerCookie(boolean revokeQuerySupported, boolean saveToDisk, int expires,
			byte[] cookieData) {
		this.revokeQuerySupported = revokeQuerySupported;
		this.saveToDisk = saveToDisk;
		this.expires = expires;
		this.cookieData = cookieData;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		int flags = buffer.readUnsignedByte();
		revokeQuerySupported = (flags & 1) == 1;
		saveToDisk = (flags & 2) == 2;
		expires = buffer.readInt();
		int cookieLen = buffer.readUnsignedByte();
		if(cookieLen > 0) {
			cookieData = new byte[cookieLen];
			buffer.readFully(cookieData);
		}else {
			cookieData = null;
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if(cookieData != null && cookieData.length > 255) {
			throw new IOException("Cookie cannot be longer than 255 bytes!");
		}
		int flags = 0;
		if(revokeQuerySupported) flags |= 1;
		if(saveToDisk) flags |= 2;
		buffer.writeByte(flags);
		buffer.writeInt(expires);
		if(cookieData != null) {
			buffer.writeByte(cookieData.length);
			buffer.write(cookieData);
		}else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 6 + (cookieData != null ? cookieData.length : 0);
	}

}