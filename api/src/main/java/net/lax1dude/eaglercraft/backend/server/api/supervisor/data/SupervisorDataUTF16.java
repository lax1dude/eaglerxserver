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

package net.lax1dude.eaglercraft.backend.server.api.supervisor.data;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;

public class SupervisorDataUTF16 implements ISupervisorData {

	@Nullable
	public String value;

	public SupervisorDataUTF16() {
	}

	public SupervisorDataUTF16(@Nullable String value) {
		this.value = value;
	}

	@Override
	public void write(@Nonnull GamePacketOutputBuffer buffer) throws IOException {
		if(value != null) {
			int len = value.length();
			buffer.writeVarInt(len + 1);
			for(int i = 0; i < len; ++i) {
				buffer.writeChar(value.charAt(i));
			}
		}else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void read(@Nonnull GamePacketInputBuffer buffer) throws IOException {
		int len = buffer.readVarInt();
		if(len > 0) {
			char[] tmp = new char[--len];
			for(int i = 0; i < len; ++i) {
				tmp[i] = buffer.readChar();
			}
			value = new String(tmp);
		}else {
			value = null;
		}
	}

}
