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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.rpc.protocol.util.PacketImageData;

import java.util.UUID;

public class CPacketRPCNotifIconRegister implements EaglerBackendRPCPacket {

	public Map<UUID,PacketImageData> notifIcons;

	public CPacketRPCNotifIconRegister() {
	}

	public CPacketRPCNotifIconRegister(Map<UUID,PacketImageData> notifIcons) {
		this.notifIcons = notifIcons;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		notifIcons = new HashMap<>();
		int cnt = buffer.readUnsignedByte();
		for(int i = 0; i < cnt; ++i) {
			UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
			PacketImageData img = PacketImageData.readRGB16(buffer);
			notifIcons.put(uuid, img);
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if(notifIcons != null && !notifIcons.isEmpty()) {
			int l = notifIcons.size();
			if(l > 255) {
				throw new IOException("Too many notification icons in packet! (Max is 255, got " + l + " total)");
			}
			buffer.writeByte(l);
			for(Entry<UUID,PacketImageData> etr : notifIcons.entrySet()) {
				UUID uuid = etr.getKey();
				buffer.writeLong(uuid.getMostSignificantBits());
				buffer.writeLong(uuid.getLeastSignificantBits());
				PacketImageData.writeRGB16(buffer, etr.getValue());
			}
		}else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		int i = 1 + (notifIcons.size() << 4);
		for(PacketImageData dat : notifIcons.values()) {
			i += dat.getByteLengthRGB16();
		}
		return i;
	}

}