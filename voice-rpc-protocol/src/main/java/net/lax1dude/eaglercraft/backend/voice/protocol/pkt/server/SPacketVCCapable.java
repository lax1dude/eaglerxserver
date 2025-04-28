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

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;

public class SPacketVCCapable implements EaglerVCPacket {

	public int version;
	public boolean allowed;
	public boolean overrideICE;
	public String[] iceServers;

	public SPacketVCCapable() {
	}

	public SPacketVCCapable(int version, boolean allowed, boolean overrideICE, String[] iceServers) {
		this.version = version;
		this.allowed = allowed;
		this.overrideICE = overrideICE;
		this.iceServers = iceServers;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		version = buffer.readUnsignedByte();
		int numIce = buffer.readUnsignedByte();
		allowed = (numIce & 128) != 0;
		overrideICE = (numIce & 64) != 0;
		numIce &= 63;
		iceServers = new String[numIce];
		for (int i = 0; i < iceServers.length; ++i) {
			iceServers[i] = EaglerVCPacket.readString(buffer, 1024, true, StandardCharsets.UTF_8);
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		int j = 0;
		if (allowed)
			j |= 128;
		if (overrideICE)
			j |= 64;
		buffer.writeByte(version);
		int cnt;
		if (iceServers != null && (cnt = iceServers.length) > 0) {
			if (cnt > 63) {
				throw new IOException("Too many STUN/TURN servers sent! (" + cnt + ", max is 63!)");
			}
			buffer.writeByte(cnt | j);
			for (int i = 0; i < cnt; ++i) {
				EaglerVCPacket.writeString(buffer, iceServers[i], true, StandardCharsets.UTF_8);
			}
		} else {
			buffer.writeByte(j);
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
