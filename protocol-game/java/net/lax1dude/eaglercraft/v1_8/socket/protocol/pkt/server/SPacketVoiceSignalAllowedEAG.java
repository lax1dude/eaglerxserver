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
public class SPacketVoiceSignalAllowedEAG implements GameMessagePacket {

	public boolean allowed;
	public String[] iceServers;

	public SPacketVoiceSignalAllowedEAG() {
	}

	public SPacketVoiceSignalAllowedEAG(boolean allowed, String[] iceServers) {
		this.allowed = allowed;
		this.iceServers = iceServers;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		allowed = buffer.readBoolean();
		if(allowed) {
			int numIce = buffer.readVarInt();
			if(numIce > 64) {
				throw new IOException("Too many STUN/TURN servers recieved! (" + numIce + ", max is 64!)");
			}
			iceServers = new String[numIce];
			for(int i = 0; i < iceServers.length; ++i) {
				iceServers[i] = buffer.readStringMC(1024);
			}
		}else {
			iceServers = null;
		}
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		if(allowed && iceServers.length > 64) {
			throw new IOException("Too many STUN/TURN servers to send! (" + iceServers.length + ", max is 64!)");
		}
		buffer.writeBoolean(allowed);
		if(allowed) {
			buffer.writeVarInt(iceServers.length);
			for(int i = 0; i < iceServers.length; ++i) {
				buffer.writeStringMC(iceServers[i]);
			}
		}
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return -1;
	}

}
