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
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class SPacketRPCEventWebViewOpenClose implements EaglerBackendRPCPacket {

	public boolean channelOpen;
	public String channelName;

	public SPacketRPCEventWebViewOpenClose() {
	}

	public SPacketRPCEventWebViewOpenClose(boolean channelOpen, String channelName) {
		this.channelOpen = channelOpen;
		this.channelName = channelName;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		channelOpen = buffer.readBoolean();
		byte[] nameBytes = new byte[buffer.readUnsignedByte()];
		buffer.readFully(nameBytes);
		channelName = new String(nameBytes, StandardCharsets.US_ASCII);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if (channelName == null || channelName.length() == 0) {
			throw new IOException("Channel name cannot be empty!");
		}
		if (channelName.length() > 255) {
			throw new IOException(
					"Channel name cannot be more than 255 chars! (got " + channelName.length() + " chars)");
		}
		buffer.writeBoolean(channelOpen);
		byte[] nameBytes = channelName.getBytes(StandardCharsets.US_ASCII);
		buffer.writeByte(nameBytes.length);
		buffer.write(nameBytes);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 2 + channelName.length();
	}

}