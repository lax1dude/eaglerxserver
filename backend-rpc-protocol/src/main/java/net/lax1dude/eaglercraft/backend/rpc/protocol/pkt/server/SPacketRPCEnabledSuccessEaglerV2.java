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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class SPacketRPCEnabledSuccessEaglerV2 implements EaglerBackendRPCPacket {

	public static class ExtCapability {

		public final UUID uuid;
		public final int version;

		public ExtCapability(UUID uuid, int version) {
			this.uuid = uuid;
			this.version = version;
		}

	}

	public int selectedRPCProtocol;
	public int minecraftProtocol;
	public int supervisorNode;
	public int eaglerHandshake;
	public int eaglerProtocol;
	public int eaglerRewindProtocol;
	public int eaglerStandardCaps;
	public byte[] eaglerStandardCapsVersions;
	public Collection<ExtCapability> eaglerExtendedCaps;

	public SPacketRPCEnabledSuccessEaglerV2() {
	}

	public SPacketRPCEnabledSuccessEaglerV2(int selectedRPCProtocol, int minecraftProtocol, int supervisorNode,
			int eaglerHandshake, int eaglerProtocol, int eaglerRewindProtocol, int eaglerStandardCaps,
			byte[] eaglerStandardCapsVersions, Collection<ExtCapability> eaglerExtendedCaps) {
		this.selectedRPCProtocol = selectedRPCProtocol;
		this.minecraftProtocol = minecraftProtocol;
		this.supervisorNode = supervisorNode;
		this.eaglerHandshake = eaglerHandshake;
		this.eaglerProtocol = eaglerProtocol;
		this.eaglerRewindProtocol = eaglerRewindProtocol;
		this.eaglerStandardCaps = eaglerStandardCaps;
		this.eaglerStandardCapsVersions = eaglerStandardCapsVersions;
		this.eaglerExtendedCaps = eaglerExtendedCaps;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		selectedRPCProtocol = buffer.readUnsignedShort();
		minecraftProtocol = buffer.readInt();
		supervisorNode = buffer.readInt();
		eaglerHandshake = buffer.readUnsignedShort();
		eaglerProtocol = buffer.readUnsignedShort();
		int rw = buffer.readUnsignedByte();
		if(rw == 255) {
			rw = -1;
		}
		eaglerRewindProtocol = rw;
		eaglerStandardCaps = buffer.readInt();
		eaglerStandardCapsVersions = new byte[Integer.bitCount(eaglerStandardCaps)];
		buffer.readFully(eaglerStandardCapsVersions);
		int extCapCount = buffer.readUnsignedByte();
		if(extCapCount > 0) {
			ExtCapability[] res = new ExtCapability[extCapCount];
			for(int i = 0; i < extCapCount; ++i) {
				res[i] = new ExtCapability(new UUID(buffer.readLong(), buffer.readLong()), buffer.readUnsignedByte());
			}
			eaglerExtendedCaps = Arrays.asList(res);
		}else {
			eaglerExtendedCaps = null;
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		int cnt = Integer.bitCount(eaglerStandardCaps);
		if(!(cnt == 0 && eaglerStandardCapsVersions == null) && cnt != eaglerStandardCapsVersions.length) {
			throw new IOException("Refusing to write an invalid number of standard capabilities");
		}
		buffer.writeShort(selectedRPCProtocol);
		buffer.writeInt(minecraftProtocol);
		buffer.writeInt(supervisorNode);
		buffer.writeShort(eaglerHandshake);
		buffer.writeShort(eaglerProtocol);
		buffer.writeByte(eaglerRewindProtocol);
		buffer.writeInt(eaglerStandardCaps);
		buffer.write(eaglerStandardCapsVersions);
		if(eaglerExtendedCaps != null) {
			int cnt2 = eaglerExtendedCaps.size();
			buffer.writeByte(cnt2);
			if(cnt2 > 0) {
				if(eaglerExtendedCaps instanceof RandomAccess) {
					List<ExtCapability> lst = (List<ExtCapability>) eaglerExtendedCaps;
					for(int i = 0; i < cnt2; ++i) {
						ExtCapability extCap = lst.get(i);
						buffer.writeLong(extCap.uuid.getMostSignificantBits());
						buffer.writeLong(extCap.uuid.getLeastSignificantBits());
						buffer.writeByte(extCap.version);
					}
				}else {
					for(ExtCapability extCap : eaglerExtendedCaps) {
						buffer.writeLong(extCap.uuid.getMostSignificantBits());
						buffer.writeLong(extCap.uuid.getLeastSignificantBits());
						buffer.writeByte(extCap.version);
					}
				}
			}
		}else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		int l = 20 + Integer.bitCount(eaglerStandardCaps);
		if(eaglerExtendedCaps != null) {
			l += eaglerExtendedCaps.size() * 17;
		}
		return l;
	}

}