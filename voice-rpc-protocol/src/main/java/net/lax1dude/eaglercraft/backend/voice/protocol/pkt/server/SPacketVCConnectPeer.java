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

package net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;

public class SPacketVCConnectPeer implements EaglerVCPacket {

	public long uuidMost;
	public long uuidLeast;
	public boolean offer;

	public SPacketVCConnectPeer() {
	}

	public SPacketVCConnectPeer(long uuidMost, long uuidLeast, boolean offer) {
		this.uuidMost = uuidMost;
		this.uuidLeast = uuidLeast;
		this.offer = offer;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		uuidMost = buffer.readLong();
		uuidLeast = buffer.readLong();
		offer = buffer.readBoolean();
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeLong(uuidMost);
		buffer.writeLong(uuidLeast);
		buffer.writeBoolean(offer);
	}

	@Override
	public void handlePacket(EaglerVCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 17;
	}

}
