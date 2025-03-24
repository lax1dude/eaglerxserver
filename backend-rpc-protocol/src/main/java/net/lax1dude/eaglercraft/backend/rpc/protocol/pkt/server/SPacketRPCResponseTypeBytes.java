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

public class SPacketRPCResponseTypeBytes implements EaglerBackendRPCPacket {

	public int requestID;
	public byte[] response;

	public SPacketRPCResponseTypeBytes() {
	}

	public SPacketRPCResponseTypeBytes(int requestID, byte[] response) {
		this.requestID = requestID;
		this.response = response;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		requestID = buffer.readInt();
		response = new byte[buffer.readUnsignedShort()];
		buffer.readFully(response);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if(response.length > 32720) {
			throw new IOException("Response is too long, max is 32720 bytes! (got " + response.length + " bytes)");
		}
		buffer.writeInt(requestID);
		buffer.writeShort(response.length);
		buffer.write(response);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 6 + response.length;
	}

}