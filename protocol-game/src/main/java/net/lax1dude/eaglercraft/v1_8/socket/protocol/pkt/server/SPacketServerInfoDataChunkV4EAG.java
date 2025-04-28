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

public class SPacketServerInfoDataChunkV4EAG implements GameMessagePacket {

	public boolean lastChunk;
	public int seqId;
	public int finalSize;
	public byte[] finalHash;
	public byte[] data;

	public SPacketServerInfoDataChunkV4EAG() {
	}

	public SPacketServerInfoDataChunkV4EAG(boolean lastChunk, int seqId, byte[] finalHash, int finalSize, byte[] data) {
		this.lastChunk = lastChunk;
		this.seqId = seqId;
		this.finalHash = finalHash;
		this.finalSize = finalSize;
		this.data = data;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		lastChunk = buffer.readBoolean();
		seqId = buffer.readVarInt();
		finalSize = buffer.readVarInt();
		finalHash = new byte[20];
		buffer.readFully(finalHash);
		data = new byte[buffer.readVarInt()];
		buffer.readFully(data);
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		if (finalHash.length != 20) {
			throw new IOException("Hash must be 20 bytes! (" + finalHash.length + " given)");
		}
		buffer.writeBoolean(lastChunk);
		buffer.writeVarInt(seqId);
		buffer.writeVarInt(finalSize);
		buffer.write(finalHash);
		buffer.writeVarInt(data.length);
		buffer.write(data);
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 21 + GamePacketOutputBuffer.getVarIntSize(finalSize) + GamePacketOutputBuffer.getVarIntSize(seqId)
				+ GamePacketOutputBuffer.getVarIntSize(data.length) + data.length;
	}

}
