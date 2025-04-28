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

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCResetPlayerMulti implements EaglerBackendRPCPacket {

	public boolean resetSkin;
	public boolean resetCape;
	public boolean resetFNAWForce;
	public boolean notifyOtherPlayers;

	public CPacketRPCResetPlayerMulti() {
	}

	public CPacketRPCResetPlayerMulti(boolean resetSkin, boolean resetCape, boolean resetFNAWForce,
			boolean notifyOtherPlayers) {
		this.resetSkin = resetSkin;
		this.resetCape = resetCape;
		this.resetFNAWForce = resetFNAWForce;
		this.notifyOtherPlayers = notifyOtherPlayers;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		int flags = buffer.readUnsignedByte();
		resetSkin = (flags & 1) != 0;
		resetCape = (flags & 2) != 0;
		resetFNAWForce = (flags & 4) != 0;
		notifyOtherPlayers = (flags & 8) != 0;
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		int flags = 0;
		if (resetSkin)
			flags |= 1;
		if (resetCape)
			flags |= 2;
		if (resetFNAWForce)
			flags |= 4;
		if (notifyOtherPlayers)
			flags |= 8;
		buffer.writeByte(flags);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 1;
	}

}