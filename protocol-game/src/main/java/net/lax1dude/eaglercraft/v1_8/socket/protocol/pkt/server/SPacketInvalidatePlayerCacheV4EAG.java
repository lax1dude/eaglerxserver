package net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class SPacketInvalidatePlayerCacheV4EAG implements GameMessagePacket {

	public Collection<InvalidateRequest> players;

	public static class InvalidateRequest {
		
		public final boolean invalidateSkin;
		public final boolean invalidateCape;
		public final long uuidMost;
		public final long uuidLeast;
		
		public InvalidateRequest(boolean invalidateSkin, boolean invalidateCape, long uuidMost, long uuidLeast) {
			this.invalidateSkin = invalidateSkin;
			this.invalidateCape = invalidateCape;
			this.uuidMost = uuidMost;
			this.uuidLeast = uuidLeast;
		}
		
	}

	public SPacketInvalidatePlayerCacheV4EAG() {
	}

	public SPacketInvalidatePlayerCacheV4EAG(Collection<InvalidateRequest> players) {
		this.players = players;
	}

	public SPacketInvalidatePlayerCacheV4EAG(InvalidateRequest... players) {
		this.players = Arrays.asList(players);
	}

	public SPacketInvalidatePlayerCacheV4EAG(boolean invalidateSkin, boolean invalidateCape, long uuidMost, long uuidLeast) {
		this.players = Arrays.asList(new InvalidateRequest(invalidateSkin, invalidateCape, uuidMost, uuidLeast));
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		int cnt = buffer.readVarInt();
		List<InvalidateRequest> userList = (List<InvalidateRequest>)(players = new ArrayList<>(cnt));
		if(cnt > 0) {
			for(int i = 0; i < cnt; ++i) {
				int flags = buffer.readUnsignedByte();
				userList.add(new InvalidateRequest((flags & 1) != 0, (flags & 2) != 0, buffer.readLong(), buffer.readLong()));
			}
		}
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		if(players == null || players.size() == 0) {
			buffer.write(0);
		}else {
			if(players instanceof RandomAccess) {
				List<InvalidateRequest> userList = (List<InvalidateRequest>)players;
				int cnt = userList.size();
				buffer.writeVarInt(cnt);
				for(int i = 0; i < cnt; ++i) {
					InvalidateRequest dt = userList.get(i);
					buffer.writeByte((dt.invalidateSkin ? 1 : 0) | (dt.invalidateCape ? 2 : 0));
					buffer.writeLong(dt.uuidMost);
					buffer.writeLong(dt.uuidLeast);
				}
			}else {
				buffer.writeVarInt(players.size());
				for(InvalidateRequest dt : players) {
					buffer.writeByte((dt.invalidateSkin ? 1 : 0) | (dt.invalidateCape ? 2 : 0));
					buffer.writeLong(dt.uuidMost);
					buffer.writeLong(dt.uuidLeast);
				}
			}
		}
	}

	@Override
	public void handlePacket(GameMessageHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		int cnt = players.size();
		return GamePacketOutputBuffer.getVarIntSize(cnt) + 17 * cnt;
	}

}
