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

package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class SPacketRPCResponseTypeCookie implements EaglerBackendRPCPacket {

	public int requestID;
	public boolean cookiesEnabled;
	public byte[] cookieData;

	public SPacketRPCResponseTypeCookie() {
	}

	public SPacketRPCResponseTypeCookie(int requestID, boolean cookiesEnabled, byte[] cookieData) {
		this.requestID = requestID;
		this.cookiesEnabled = cookiesEnabled;
		this.cookieData = cookieData;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		requestID = buffer.readInt();
		int flags = buffer.readUnsignedByte();
		cookiesEnabled = (flags & 1) != 0;
		int len = cookiesEnabled ? buffer.readUnsignedByte() : 0;
		if (cookiesEnabled && len > 0) {
			cookieData = new byte[len];
			buffer.readFully(cookieData);
		} else {
			cookieData = null;
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeInt(requestID);
		if (cookiesEnabled) {
			buffer.writeByte(1);
			if (cookieData != null && cookieData.length > 0) {
				if (cookieData.length > 255) {
					throw new IOException(
							"Cookie is too long, max is 255 bytes! (got " + cookieData.length + " bytes)");
				}
				buffer.writeByte(cookieData.length);
				buffer.write(cookieData);
			} else {
				buffer.writeByte(0);
			}
		} else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return cookiesEnabled ? (cookieData != null ? (6 + cookieData.length) : 6) : 5;
	}

}