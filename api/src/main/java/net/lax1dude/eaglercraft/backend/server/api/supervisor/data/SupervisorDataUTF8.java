package net.lax1dude.eaglercraft.backend.server.api.supervisor.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketInputBuffer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePacketOutputBuffer;

/**
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
public class SupervisorDataUTF8 implements ISupervisorData {

	@Nullable
	public String value;

	public SupervisorDataUTF8() {
	}

	public SupervisorDataUTF8(@Nullable String value) {
		this.value = value;
	}

	@Override
	public void write(@Nonnull GamePacketOutputBuffer buffer) throws IOException {
		if(value != null) {
			byte[] data = value.getBytes(StandardCharsets.UTF_8);
			buffer.writeVarInt(data.length + 1);
			buffer.write(data);
		}else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void read(@Nonnull GamePacketInputBuffer buffer) throws IOException {
		int len = buffer.readVarInt();
		if(len > 0) {
			byte[] arr = new byte[len - 1];
			buffer.readFully(arr);
			value = new String(arr, StandardCharsets.UTF_8);
		}else {
			value = null;
		}
	}

}