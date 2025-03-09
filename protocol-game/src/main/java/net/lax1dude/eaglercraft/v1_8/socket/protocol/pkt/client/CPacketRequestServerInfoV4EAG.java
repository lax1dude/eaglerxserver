package net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client;

import java.io.IOException;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

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
public class CPacketRequestServerInfoV4EAG implements GameMessagePacket {

	public byte[] requestHash;

	public CPacketRequestServerInfoV4EAG() {
	}

	public CPacketRequestServerInfoV4EAG(byte[] requestHash) {
		this.requestHash = requestHash;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		requestHash = new byte[20];
		buffer.readFully(requestHash);
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		if(requestHash.length != 20) {
			throw new IOException("Hash must be 20 bytes! (" + requestHash.length + " given)");
		}
		buffer.write(requestHash);
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 20;
	}

}
