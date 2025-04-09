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

public class SPacketSvRPCResultSuccess implements EaglerSupervisorPacket {

	public UUID requestUUID;
	public byte[] dataBuffer;

	public SPacketSvRPCResultSuccess() {
	}

	public SPacketSvRPCResultSuccess(UUID requestUUID, byte[] dataBuffer) {
		this.requestUUID = requestUUID;
		this.dataBuffer = dataBuffer;
	}

	@Override
	public void readPacket(ByteBuf buffer) {
		requestUUID = new UUID(buffer.readLong(), buffer.readLong());
		int len = EaglerSupervisorPacket.readVarInt(buffer);
		if(len > 0) {
			dataBuffer = new byte[len];
			buffer.readBytes(dataBuffer);
		}else {
			dataBuffer = null;
		}
	}

	@Override
	public void writePacket(ByteBuf buffer) {
		buffer.writeLong(requestUUID.getMostSignificantBits());
		buffer.writeLong(requestUUID.getLeastSignificantBits());
		if(dataBuffer != null && dataBuffer.length > 0) {
			EaglerSupervisorPacket.writeVarInt(buffer, dataBuffer.length);
			buffer.writeBytes(dataBuffer);
		}else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(EaglerSupervisorHandler handler) {
		handler.handleServer(this);
	}

}