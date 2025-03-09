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
public class CPacketVoiceSignalDisconnectV3EAG implements GameMessagePacket {

	public boolean isPeerType;
	public long uuidMost;
	public long uuidLeast;

	public CPacketVoiceSignalDisconnectV3EAG() {
	}

	public CPacketVoiceSignalDisconnectV3EAG(boolean isPeerType, long uuidMost, long uuidLeast) {
		this.isPeerType = isPeerType;
		this.uuidMost = uuidMost;
		this.uuidLeast = uuidLeast;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		if(buffer.available() > 0) {
			isPeerType = true;
			uuidMost = buffer.readLong();
			uuidLeast = buffer.readLong();
		}else {
			isPeerType = false;
		}
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		if(isPeerType) {
			buffer.writeLong(uuidMost);
			buffer.writeLong(uuidLeast);
		}
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return isPeerType ? 16 : 0;
	}

}
