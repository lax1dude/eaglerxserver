package net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server;

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
public class SPacketVoiceSignalICEEAG implements GameMessagePacket {

	public long uuidMost;
	public long uuidLeast;
	public byte[] ice;

	public SPacketVoiceSignalICEEAG() {
	}

	public SPacketVoiceSignalICEEAG(long uuidMost, long uuidLeast, byte[] ice) {
		this.uuidMost = uuidMost;
		this.uuidLeast = uuidLeast;
		this.ice = ice;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		uuidMost = buffer.readLong();
		uuidLeast = buffer.readLong();
		int iceLen = buffer.readVarInt();
		if(iceLen > 32750) {
			throw new IOException("Voice signal packet ICE too long!");
		}
		ice = new byte[iceLen];
		buffer.readFully(ice);
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		if(ice.length > 32750) {
			throw new IOException("Voice signal packet ICE too long!");
		}
		buffer.writeLong(uuidMost);
		buffer.writeLong(uuidLeast);
		buffer.writeVarInt(ice.length);
		buffer.write(ice);
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 16 + GamePacketOutputBuffer.getArrayMCSize(ice.length);
	}

}
