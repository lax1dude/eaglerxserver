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

package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCGetCapeByURLV2 implements EaglerBackendRPCPacket {

	public int requestID;
	public String capeURL;

	public CPacketRPCGetCapeByURLV2() {
	}

	public CPacketRPCGetCapeByURLV2(int requestID, String capeURL) {
		this.requestID = requestID;
		this.capeURL = capeURL;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		requestID = buffer.readInt();
		capeURL = EaglerBackendRPCPacket.readString(buffer, 65535, true, StandardCharsets.US_ASCII);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeInt(requestID);
		EaglerBackendRPCPacket.writeString(buffer, capeURL, true, StandardCharsets.US_ASCII);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 6 + capeURL.length();
	}

}
