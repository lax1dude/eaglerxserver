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

package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCSetPlayerTexturesPresetV2 implements EaglerBackendRPCPacket {

	public boolean notifyOthers;
	public int presetSkinId;
	public int presetCapeId;

	public CPacketRPCSetPlayerTexturesPresetV2() {
	}

	public CPacketRPCSetPlayerTexturesPresetV2(boolean notifyOthers, int presetSkinId, int presetCapeId) {
		this.notifyOthers = notifyOthers;
		this.presetSkinId = presetSkinId;
		this.presetCapeId = presetCapeId;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		notifyOthers = buffer.readBoolean();
		presetSkinId = buffer.readInt();
		presetCapeId = buffer.readInt();
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeBoolean(notifyOthers);
		buffer.writeInt(presetSkinId);
		buffer.writeInt(presetCapeId);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 9;
	}

}
