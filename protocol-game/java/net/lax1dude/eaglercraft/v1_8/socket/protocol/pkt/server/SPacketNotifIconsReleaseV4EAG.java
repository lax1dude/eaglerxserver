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
public class SPacketNotifIconsReleaseV4EAG implements GameMessagePacket {

	public static class DestroyIcon {

		public long uuidMost;
		public long uuidLeast;

		public DestroyIcon(long uuidMost, long uuidLeast) {
			this.uuidMost = uuidMost;
			this.uuidLeast = uuidLeast;
		}

	}

	public Collection<DestroyIcon> iconsToDestroy = null;

	public SPacketNotifIconsReleaseV4EAG() {
	}

	public SPacketNotifIconsReleaseV4EAG(Collection<DestroyIcon> iconsToDestroy) {
		this.iconsToDestroy = iconsToDestroy;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		int len = buffer.readVarInt();
		iconsToDestroy = new ArrayList<>(len);
		for(int i = 0; i < len; ++i) {
			iconsToDestroy.add(new DestroyIcon(buffer.readLong(), buffer.readLong()));
		}
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		if(iconsToDestroy instanceof RandomAccess) {
			int len = iconsToDestroy.size();
			buffer.writeVarInt(len);
			List<DestroyIcon> vigg = (List<DestroyIcon>)iconsToDestroy;
			for(int i = 0; i < len; ++i) {
				DestroyIcon icn = vigg.get(i);
				buffer.writeLong(icn.uuidMost);
				buffer.writeLong(icn.uuidLeast);
			}
		}else {
			buffer.writeVarInt(iconsToDestroy.size());
			for(DestroyIcon icn : iconsToDestroy) {
				buffer.writeLong(icn.uuidMost);
				buffer.writeLong(icn.uuidLeast);
			}
		}
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		int len = iconsToDestroy.size();
		return GamePacketOutputBuffer.getVarIntSize(len) + (len << 4);
	}

}
