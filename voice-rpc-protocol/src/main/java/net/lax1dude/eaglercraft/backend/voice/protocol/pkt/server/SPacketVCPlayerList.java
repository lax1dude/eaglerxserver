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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;

public class SPacketVCPlayerList implements EaglerVCPacket {

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

	public SPacketVCPlayerList() {
	}

	public SPacketVCPlayerList(Collection<UserData> users) {
		this.users = users;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		int cnt = buffer.readInt();
		List<UserData> userList = (List<UserData>) (users = new ArrayList<>(cnt));
		if (cnt > 0) {
			for (int i = 0; i < cnt; ++i) {
				userList.add(new UserData(buffer.readLong(), buffer.readLong(), null));
			}
			for (int i = 0; i < cnt; ++i) {
				userList.get(i).username = EaglerVCPacket.readString(buffer, 16, false, StandardCharsets.US_ASCII);
			}
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if (users == null || users.size() == 0) {
			buffer.writeByte(0);
		} else {
			if (users instanceof RandomAccess) {
				List<UserData> userList = (List<UserData>) users;
				int cnt = userList.size();
				buffer.writeInt(cnt);
				for (int i = 0; i < cnt; ++i) {
					UserData dt = userList.get(i);
					buffer.writeLong(dt.uuidMost);
					buffer.writeLong(dt.uuidLeast);
				}
				for (int i = 0; i < cnt; ++i) {
					EaglerVCPacket.writeString(buffer, userList.get(i).username, false, StandardCharsets.US_ASCII);
				}
			} else {
				buffer.writeInt(users.size());
				for (UserData dt : users) {
					buffer.writeLong(dt.uuidMost);
					buffer.writeLong(dt.uuidLeast);
				}
				for (UserData dt : users) {
					EaglerVCPacket.writeString(buffer, dt.username, false, StandardCharsets.US_ASCII);
				}
			}
		}
	}

	@Override
	public void handlePacket(EaglerVCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return -1;
	}

}
