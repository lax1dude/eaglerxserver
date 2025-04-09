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

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;

public class SPacketSvRPCResultMulti implements EaglerSupervisorPacket {

	public static class ResultEntry {

		public final int nodeId;
		public final int status;
		public final byte[] dataBuffer;

		protected ResultEntry(int nodeId, int status, byte[] dataBuffer) {
			this.nodeId = nodeId;
			this.status = status;
			this.dataBuffer = dataBuffer;
		}

		public static ResultEntry success(int nodeId, byte[] dataBuffer) {
			return new ResultEntry(nodeId, 0, dataBuffer);
		}

		public static ResultEntry failure(int nodeId, int failureCode) {
			return new ResultEntry(nodeId, failureCode + 1, null);
		}

	}

	public UUID requestUUID;
	public Collection<ResultEntry> results;

	public SPacketSvRPCResultMulti() {
	}

	public SPacketSvRPCResultMulti(UUID requestUUID, Collection<ResultEntry> results) {
		this.requestUUID = requestUUID;
		this.results = results;
	}

	@Override
	public void readPacket(ByteBuf buffer) {
		requestUUID = new UUID(buffer.readLong(), buffer.readLong());
		int cnt = EaglerSupervisorPacket.readVarInt(buffer);
		results = new ArrayList<>(cnt);
		for(int i = 0; i < cnt; ++i) {
			int nodeId = EaglerSupervisorPacket.readVarInt(buffer);
			int var2 = EaglerSupervisorPacket.readVarInt(buffer);
			if(var2 > 0) {
				byte[] data = null;
				if(var2 > 1) {
					data = new byte[var2 - 1];
					buffer.readBytes(data);
				}
				results.add(ResultEntry.success(nodeId, data));
			}else {
				results.add(ResultEntry.failure(nodeId, buffer.readUnsignedByte()));
			}
		}
	}

	@Override
	public void writePacket(ByteBuf buffer) {
		buffer.writeLong(requestUUID.getMostSignificantBits());
		buffer.writeLong(requestUUID.getLeastSignificantBits());
		EaglerSupervisorPacket.writeVarInt(buffer, results.size());
		for(ResultEntry etr : results) {
			EaglerSupervisorPacket.writeVarInt(buffer, etr.nodeId);
			if(etr.status == 0) {
				if(etr.dataBuffer != null && etr.dataBuffer.length > 0) {
					EaglerSupervisorPacket.writeVarInt(buffer, etr.dataBuffer.length + 1);
					buffer.writeBytes(etr.dataBuffer);
				}else {
					buffer.writeByte(1);
				}
			}else {
				buffer.writeByte(0);
				buffer.writeByte(etr.status - 1);
			}
		}
	}

	@Override
	public void handlePacket(EaglerSupervisorHandler handler) {
		handler.handleServer(this);
	}

}