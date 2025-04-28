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
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;

public class SPacketVCICECandidate implements EaglerVCPacket {

	public long uuidMost;
	public long uuidLeast;
	public byte[] ice;

	public SPacketVCICECandidate() {
	}

	public SPacketVCICECandidate(long uuidMost, long uuidLeast, byte[] ice) {
		this.uuidMost = uuidMost;
		this.uuidLeast = uuidLeast;
		this.ice = ice;
	}

	public SPacketVCICECandidate(long uuidMost, long uuidLeast, String ice) {
		this.uuidMost = uuidMost;
		this.uuidLeast = uuidLeast;
		this.ice = ice.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		uuidMost = buffer.readLong();
		uuidLeast = buffer.readLong();
		int descLen = buffer.readUnsignedShort();
		if (descLen > 32750) {
			throw new IOException("Voice signal packet ICE too long!");
		}
		ice = new byte[descLen];
		buffer.readFully(ice);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if (ice.length > 32750) {
			throw new IOException("Voice signal packet ICE too long!");
		}
		buffer.writeLong(uuidMost);
		buffer.writeLong(uuidLeast);
		buffer.writeShort(ice.length);
		buffer.write(ice);
	}

	@Override
	public void handlePacket(EaglerVCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 18 + ice.length;
	}

}
