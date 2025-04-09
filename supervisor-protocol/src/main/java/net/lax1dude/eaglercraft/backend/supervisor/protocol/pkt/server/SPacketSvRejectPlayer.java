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

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;

public class SPacketSvRejectPlayer implements EaglerSupervisorPacket {

	public static final int CAUSE_UNKNOWN = 0x00;
	public static final int CAUSE_DUPLICATE_USERNAME = 0x01;
	public static final int CAUSE_DUPLICATE_UUID = 0x02;

	public UUID playerUUID;
	public int cause;

	public SPacketSvRejectPlayer() {
	}

	public SPacketSvRejectPlayer(UUID playerUUID, int cause) {
		this.playerUUID = playerUUID;
		this.cause = cause;
	}

	@Override
	public void readPacket(ByteBuf buffer) {
		playerUUID = new UUID(buffer.readLong(), buffer.readLong());
		cause = buffer.readUnsignedByte();
	}

	@Override
	public void writePacket(ByteBuf buffer) {
		buffer.writeLong(playerUUID.getMostSignificantBits());
		buffer.writeLong(playerUUID.getLeastSignificantBits());
		buffer.writeByte(cause);
	}

	@Override
	public void handlePacket(EaglerSupervisorHandler handler) {
		handler.handleServer(this);
	}

}