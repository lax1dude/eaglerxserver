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

public class SPacketVoiceSignalGlobalEAG implements GameMessagePacket {

	public Collection<UserData> users;

	public static class UserData {

		public long uuidMost;
		public long uuidLeast;
		public String username;

		public UserData(long uuidMost, long uuidLeast, String username) {
			this.uuidMost = uuidMost;
			this.uuidLeast = uuidLeast;
			this.username = username;
		}

	}

	public SPacketVoiceSignalGlobalEAG() {
	}

	public SPacketVoiceSignalGlobalEAG(Collection<UserData> users) {
		this.users = users;
	}

	@Override
	public void readPacket(GamePacketInputBuffer buffer) throws IOException {
		int cnt = buffer.readVarInt();
		List<UserData> userList = (List<UserData>) (users = new ArrayList<>(cnt));
		if (cnt > 0) {
			for (int i = 0; i < cnt; ++i) {
				userList.add(new UserData(buffer.readLong(), buffer.readLong(), null));
			}
			if (buffer.available() > 0) {
				for (int i = 0; i < cnt; ++i) {
					userList.get(i).username = buffer.readStringMC(16);
				}
			}
		}
	}

	@Override
	public void writePacket(GamePacketOutputBuffer buffer) throws IOException {
		if (users == null || users.size() == 0) {
			buffer.write(0);
		} else {
			if (users instanceof RandomAccess) {
				List<UserData> userList = (List<UserData>) users;
				int cnt = userList.size();
				buffer.writeVarInt(cnt);
				for (int i = 0; i < cnt; ++i) {
					UserData dt = userList.get(i);
					buffer.writeLong(dt.uuidMost);
					buffer.writeLong(dt.uuidLeast);
				}
				for (int i = 0; i < cnt; ++i) {
					buffer.writeStringMC(userList.get(i).username);
				}
			} else {
				buffer.writeVarInt(users.size());
				for (UserData dt : users) {
					buffer.writeLong(dt.uuidMost);
					buffer.writeLong(dt.uuidLeast);
				}
				for (UserData dt : users) {
					buffer.writeStringMC(dt.username);
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
		return -1;
	}

}
