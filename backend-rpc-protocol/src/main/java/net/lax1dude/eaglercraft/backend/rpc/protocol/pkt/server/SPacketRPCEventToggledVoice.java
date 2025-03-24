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

package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class SPacketRPCEventToggledVoice implements EaglerBackendRPCPacket {

	public static final int VOICE_STATE_SERVER_DISABLE = 0;
	public static final int VOICE_STATE_DISABLED = 1;
	public static final int VOICE_STATE_ENABLED = 2;

	public int oldVoiceState;
	public int newVoiceState;

	public SPacketRPCEventToggledVoice() {
	}

	public SPacketRPCEventToggledVoice(int oldVoiceState, int newVoiceState) {
		this.oldVoiceState = oldVoiceState;
		this.newVoiceState = newVoiceState;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		int i = buffer.readUnsignedByte();
		oldVoiceState = (i >>> 4) & 15;
		newVoiceState = i & 15;
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeByte((oldVoiceState << 4) | newVoiceState);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return 1;
	}

}