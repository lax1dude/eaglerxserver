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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCSetPlayerCape implements EaglerBackendRPCPacket {

	public boolean notifyOthers;
	public byte[] capePacket;

	public CPacketRPCSetPlayerCape() {
	}

	public CPacketRPCSetPlayerCape(boolean notifyOthers, byte[] capePacket) {
		this.notifyOthers = notifyOthers;
		this.capePacket = capePacket;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		notifyOthers = buffer.readBoolean();
		capePacket = new byte[buffer.readUnsignedShort()];
		buffer.readFully(capePacket);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if (capePacket.length > 32720) {
			throw new IOException("Cape data cannot be longer than 32720 bytes!");
		}
		buffer.writeBoolean(notifyOthers);
		buffer.writeShort(capePacket.length);
		buffer.write(capePacket);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 3 + capePacket.length;
	}

}