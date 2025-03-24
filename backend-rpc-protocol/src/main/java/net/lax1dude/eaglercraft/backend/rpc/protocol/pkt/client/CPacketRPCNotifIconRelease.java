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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCNotifIconRelease implements EaglerBackendRPCPacket {

	public Collection<UUID> iconsToRelease;

	public CPacketRPCNotifIconRelease() {
	}

	public CPacketRPCNotifIconRelease(Collection<UUID> iconsToRelease) {
		this.iconsToRelease = iconsToRelease;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		int cnt = buffer.readUnsignedByte();
		iconsToRelease = new ArrayList<>(cnt);
		for(int i = 0; i < cnt; ++i) {
			iconsToRelease.add(new UUID(buffer.readLong(), buffer.readLong()));
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if(iconsToRelease != null && !iconsToRelease.isEmpty()) {
			int cnt = iconsToRelease.size();
			if(cnt > 255) {
				throw new IOException("Too many notification icons in packet! (Max is 255, got " + cnt + " total)");
			}
			buffer.writeByte(cnt);
			if(iconsToRelease instanceof RandomAccess) {
				List<UUID> vigg = (List<UUID>)iconsToRelease;
				for(int i = 0; i < cnt; ++i) {
					UUID uuid = vigg.get(i);
					buffer.writeLong(uuid.getMostSignificantBits());
					buffer.writeLong(uuid.getLeastSignificantBits());
				}
			}else {
				for(UUID uuid : iconsToRelease) {
					buffer.writeLong(uuid.getMostSignificantBits());
					buffer.writeLong(uuid.getLeastSignificantBits());
				}
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
		return 1 + (iconsToRelease.size() << 4);
	}

}