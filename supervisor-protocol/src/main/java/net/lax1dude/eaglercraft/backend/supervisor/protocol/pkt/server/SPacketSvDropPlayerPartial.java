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

package net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;

public class SPacketSvDropPlayerPartial implements EaglerSupervisorPacket {

	public static final int DROP_PLAYER_SKIN = 1;
	public static final int DROP_PLAYER_CAPE = 2;

	public UUID uuid;
	public String serverNotify;
	public int bitmask;

	public SPacketSvDropPlayerPartial() {
	}

	public SPacketSvDropPlayerPartial(UUID uuid, String serverNotify, int bitmask) {
		this.uuid = uuid;
		this.serverNotify = serverNotify;
		this.bitmask = bitmask;
	}

	@Override
	public void readPacket(ByteBuf buffer) {
		uuid = new UUID(buffer.readLong(), buffer.readLong());
		int notifyLen = EaglerSupervisorPacket.readVarInt(buffer);
		serverNotify = notifyLen > 0 ? buffer.readCharSequence(notifyLen, StandardCharsets.US_ASCII).toString() : null;
		bitmask = buffer.readUnsignedByte();
	}

	@Override
	public void writePacket(ByteBuf buffer) {
		buffer.writeLong(uuid.getMostSignificantBits());
		buffer.writeLong(uuid.getLeastSignificantBits());
		if (serverNotify != null && serverNotify.length() > 0) {
			byte[] asciiBytes = serverNotify.getBytes(StandardCharsets.US_ASCII);
			EaglerSupervisorPacket.writeVarInt(buffer, asciiBytes.length);
			buffer.writeBytes(asciiBytes);
		} else {
			buffer.writeByte(0);
		}
		buffer.writeByte(bitmask);
	}

	@Override
	public void handlePacket(EaglerSupervisorHandler handler) {
		handler.handleServer(this);
	}

}