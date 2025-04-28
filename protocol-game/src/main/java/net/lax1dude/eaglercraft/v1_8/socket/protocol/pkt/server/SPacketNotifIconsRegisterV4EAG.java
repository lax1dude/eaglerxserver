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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.PacketImageData;

public class SPacketNotifIconsRegisterV4EAG implements GameMessagePacket {

	public static class CreateIcon {

		public long uuidMost;
		public long uuidLeast;
		public PacketImageData imageData;

		public CreateIcon(long uuidMost, long uuidLeast, PacketImageData imageData) {
			this.uuidMost = uuidMost;
			this.uuidLeast = uuidLeast;
			this.imageData = imageData;
		}

		public int length() {
			return 16 + imageData.getByteLengthRGB16();
		}

	}

	public Collection<CreateIcon> iconsToCreate = null;

	public SPacketNotifIconsRegisterV4EAG() {
	}

	public SPacketNotifIconsRegisterV4EAG(Collection<CreateIcon> iconsToCreate) {
		this.iconsToCreate = iconsToCreate;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		int len = buffer.readVarInt();
		iconsToCreate = new ArrayList<>(len);
		for (int i = 0; i < len; ++i) {
			iconsToCreate.add(new CreateIcon(buffer.readLong(), buffer.readLong(), PacketImageData.readRGB16(buffer)));
		}
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		if (iconsToCreate instanceof RandomAccess) {
			int len = iconsToCreate.size();
			buffer.writeVarInt(len);
			List<CreateIcon> vigg = (List<CreateIcon>) iconsToCreate;
			for (int i = 0, l = vigg.size(); i < l; ++i) {
				CreateIcon icn = vigg.get(i);
				buffer.writeLong(icn.uuidMost);
				buffer.writeLong(icn.uuidLeast);
				PacketImageData.writeRGB16(buffer, icn.imageData);
			}
		} else {
			buffer.writeVarInt(iconsToCreate.size());
			for (CreateIcon icn : iconsToCreate) {
				buffer.writeLong(icn.uuidMost);
				buffer.writeLong(icn.uuidLeast);
				PacketImageData.writeRGB16(buffer, icn.imageData);
			}
		}
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		int len = GamePacketOutputBuffer.getVarIntSize(iconsToCreate.size());
		if (iconsToCreate instanceof RandomAccess) {
			List<CreateIcon> vigg = (List<CreateIcon>) iconsToCreate;
			for (int i = 0, l = vigg.size(); i < l; ++i) {
				len += vigg.get(i).length();
			}
		} else {
			for (CreateIcon icn : iconsToCreate) {
				len += icn.length();
			}
		}
		return len;
	}

}
