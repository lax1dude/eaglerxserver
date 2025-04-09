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

package net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;

public class CPacketSvRegisterPlayer implements EaglerSupervisorPacket {

	public UUID playerUUID;
	public UUID brandUUID;
	public int gameProtocol;
	public int eaglerProtocol;
	public String username;

	public CPacketSvRegisterPlayer() {
	}

	public CPacketSvRegisterPlayer(UUID playerUUID, UUID brandUUID, int gameProtocol, int eaglerProtocol, String username) {
		this.playerUUID = playerUUID;
		this.brandUUID = brandUUID;
		this.gameProtocol = gameProtocol;
		this.eaglerProtocol = eaglerProtocol;
		this.username = username;
	}

	@Override
	public void readPacket(ByteBuf buffer) {
		playerUUID = new UUID(buffer.readLong(), buffer.readLong());
		brandUUID = new UUID(buffer.readLong(), buffer.readLong());
		gameProtocol = EaglerSupervisorPacket.readVarInt(buffer);
		eaglerProtocol = EaglerSupervisorPacket.readVarInt(buffer);
		int usernameLen = buffer.readUnsignedByte();
		username = buffer.readCharSequence(usernameLen, StandardCharsets.US_ASCII).toString();
	}

	@Override
	public void writePacket(ByteBuf buffer) {
		buffer.writeLong(playerUUID.getMostSignificantBits());
		buffer.writeLong(playerUUID.getLeastSignificantBits());
		buffer.writeLong(brandUUID.getMostSignificantBits());
		buffer.writeLong(brandUUID.getLeastSignificantBits());
		EaglerSupervisorPacket.writeVarInt(buffer, gameProtocol);
		EaglerSupervisorPacket.writeVarInt(buffer, eaglerProtocol);
		byte[] usernameBytes = username.getBytes(StandardCharsets.US_ASCII);
		int len = usernameBytes.length;
		if(len > 16) {
			throw new UnsupportedOperationException("Username is longer than 16 bytes");
		}
		buffer.writeByte(len);
		buffer.writeBytes(usernameBytes);
	}

	@Override
	public void handlePacket(EaglerSupervisorHandler handler) {
		handler.handleClient(this);
	}

}