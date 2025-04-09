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

public class CPacketSvRPCExecutePlayerName implements EaglerSupervisorPacket {

	public UUID requestUUID;
	public int timeout;
	public String playerName;
	public byte[] name;
	public byte[] dataBuffer;

	public CPacketSvRPCExecutePlayerName() {
	}

	public CPacketSvRPCExecutePlayerName(UUID requestUUID, int timeout, String playerName, byte[] name, byte[] dataBuffer) {
		this.requestUUID = requestUUID;
		this.timeout = timeout;
		this.playerName = playerName;
		this.name = name;
		this.dataBuffer = dataBuffer;
	}

	public CPacketSvRPCExecutePlayerName(UUID requestUUID, int timeout, String playerName, String name, byte[] dataBuffer) {
		this.requestUUID = requestUUID;
		this.timeout = timeout;
		this.playerName = playerName;
		this.name = name.getBytes(StandardCharsets.US_ASCII);
		this.dataBuffer = dataBuffer;
	}

	@Override
	public void readPacket(ByteBuf buffer) {
		timeout = EaglerSupervisorPacket.readVarInt(buffer);
		if(timeout > 0) {
			requestUUID = new UUID(buffer.readLong(), buffer.readLong());
		}else {
			requestUUID = null;
		}
		int len = buffer.readUnsignedByte();
		playerName = buffer.readCharSequence(len, StandardCharsets.US_ASCII).toString();
		len = EaglerSupervisorPacket.readVarInt(buffer);
		name = new byte[len];
		buffer.readBytes(name);
		len = EaglerSupervisorPacket.readVarInt(buffer);
		if(len > 0) {
			dataBuffer = new byte[len];
			buffer.readBytes(dataBuffer);
		}else {
			dataBuffer = null;
		}
	}

	@Override
	public void writePacket(ByteBuf buffer) {
		EaglerSupervisorPacket.writeVarInt(buffer, timeout);
		if(timeout > 0) {
			buffer.writeLong(requestUUID.getMostSignificantBits());
			buffer.writeLong(requestUUID.getLeastSignificantBits());
		}
		byte[] nameBytes = playerName.getBytes(StandardCharsets.US_ASCII);
		int len = nameBytes.length;
		if(len > 16) {
			throw new UnsupportedOperationException("Username is longer than 16 bytes");
		}
		buffer.writeByte(len);
		buffer.writeBytes(nameBytes);
		buffer.writeByte(name.length);
		buffer.writeBytes(name);
		if(dataBuffer != null && dataBuffer.length > 0) {
			EaglerSupervisorPacket.writeVarInt(buffer, dataBuffer.length);
			buffer.writeBytes(dataBuffer);
		}else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(EaglerSupervisorHandler handler) {
		handler.handleClient(this);
	}

}