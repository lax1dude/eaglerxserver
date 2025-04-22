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

package net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;

public class CPacketVCDescription implements EaglerVCPacket {

	public long uuidMost;
	public long uuidLeast;
	public byte[] desc;

	public CPacketVCDescription() {
	}

	public CPacketVCDescription(long uuidMost, long uuidLeast, byte[] desc) {
		this.uuidMost = uuidMost;
		this.uuidLeast = uuidLeast;
		this.desc = desc;
	}

	public CPacketVCDescription(long uuidMost, long uuidLeast, String desc) {
		this.uuidMost = uuidMost;
		this.uuidLeast = uuidLeast;
		this.desc = desc.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		uuidMost = buffer.readLong();
		uuidLeast = buffer.readLong();
		int descLen = buffer.readUnsignedShort();
		if(descLen > 32750) {
			throw new IOException("Voice signal packet DESC too long!");
		}
		desc = new byte[descLen];
		buffer.readFully(desc);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if(desc.length > 32750) {
			throw new IOException("Voice signal packet DESC too long!");
		}
		buffer.writeLong(uuidMost);
		buffer.writeLong(uuidLeast);
		buffer.writeShort(desc.length);
		buffer.write(desc);
	}

	@Override
	public void handlePacket(EaglerVCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 18 + desc.length;
	}

}
