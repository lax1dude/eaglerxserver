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

import static net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket.readString;
import static net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket.writeString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCSendRawMessage implements EaglerBackendRPCPacket {

	public String messageChannel;
	public byte[] messageData;

	public CPacketRPCSendRawMessage() {
	}

	public CPacketRPCSendRawMessage(String messageChannel, byte[] messageData) {
		this.messageChannel = messageChannel;
		this.messageData = messageData;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		messageChannel = readString(buffer, 32, false, StandardCharsets.US_ASCII);
		messageData = new byte[buffer.readUnsignedShort()];
		buffer.readFully(messageData);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if(messageChannel.length() > 32) {
			throw new IOException("Message channel name cannot be longer than 32 chars!");
		}
		if(messageData.length > 32720) {
			throw new IOException("Message data cannot be longer than 32720 bytes!");
		}
		writeString(buffer, messageChannel, false, StandardCharsets.US_ASCII);
		buffer.writeShort(messageData.length);
		buffer.write(messageData);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 1 + messageChannel.length() + 2 + messageData.length;
	}

}