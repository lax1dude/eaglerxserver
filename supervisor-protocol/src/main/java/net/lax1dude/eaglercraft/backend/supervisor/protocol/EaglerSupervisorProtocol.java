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

package net.lax1dude.eaglercraft.backend.supervisor.protocol;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.*;

public enum EaglerSupervisorProtocol {
	INIT(0,
			define_CLIENT_(0x00, CPacketSvHandshake.class),
			define_SERVER_(0x01, SPacketSvHandshakeSuccess.class),
			define_SERVER_(0x02, SPacketSvHandshakeFailure.class)
	), V1(1,
			define_CLIENT_(0x03, CPacketSvPing.class),
			define_CLIENT_(0x04, CPacketSvPong.class),
			define_SERVER_(0x05, SPacketSvPing.class),
			define_SERVER_(0x06, SPacketSvPong.class),
			define_CLIENT_(0x07, CPacketSvProxyBrand.class),
			define_CLIENT_(0x08, CPacketSvProxyStatus.class),
			define_CLIENT_(0x09, CPacketSvRegisterPlayer.class),
			define_SERVER_(0x0A, SPacketSvAcceptPlayer.class),
			define_SERVER_(0x0B, SPacketSvRejectPlayer.class),
			define_CLIENT_(0x0C, CPacketSvDropPlayer.class),
			define_CLIENT_(0x0D, CPacketSvDropPlayerPartial.class),
			define_SERVER_(0x0E, SPacketSvTotalPlayerCount.class),
			define_SERVER_(0x0F, SPacketSvDropPlayer.class),
			define_SERVER_(0x10, SPacketSvDropPlayerPartial.class),
			define_SERVER_(0x11, SPacketSvDropAllPlayers.class),
			define_SERVER_(0x12, SPacketSvPlayerNodeID.class),
			define_CLIENT_(0x13, CPacketSvGetOtherSkin.class),
			define_CLIENT_(0x14, CPacketSvGetSkinByURL.class),
			define_SERVER_(0x15, SPacketSvGetOtherSkin.class),
			define_CLIENT_(0x16, CPacketSvOtherSkinPreset.class),
			define_CLIENT_(0x17, CPacketSvOtherSkinCustom.class),
			define_CLIENT_(0x18, CPacketSvOtherSkinURL.class),
			define_SERVER_(0x19, SPacketSvOtherSkinPreset.class),
			define_SERVER_(0x1A, SPacketSvOtherSkinCustom.class),
			define_SERVER_(0x1B, SPacketSvOtherSkinError.class),
			define_CLIENT_(0x1C, CPacketSvGetOtherCape.class),
			define_CLIENT_(0x1D, CPacketSvGetCapeByURL.class),
			define_SERVER_(0x1E, SPacketSvGetOtherCape.class),
			define_CLIENT_(0x1F, CPacketSvOtherCapePreset.class),
			define_CLIENT_(0x20, CPacketSvOtherCapeCustom.class),
			define_CLIENT_(0x21, CPacketSvOtherCapeURL.class),
			define_SERVER_(0x22, SPacketSvOtherCapePreset.class),
			define_SERVER_(0x23, SPacketSvOtherCapeCustom.class),
			define_SERVER_(0x24, SPacketSvOtherCapeError.class),
			define_CLIENT_(0x25, CPacketSvGetClientBrandUUID.class),
			define_SERVER_(0x26, SPacketSvClientBrandError.class),
			define_CLIENT_(0x27, CPacketSvRPCExecuteAll.class),
			define_CLIENT_(0x28, CPacketSvRPCExecuteNode.class),
			define_CLIENT_(0x29, CPacketSvRPCExecutePlayerName.class),
			define_CLIENT_(0x2A, CPacketSvRPCExecutePlayerUUID.class),
			define_SERVER_(0x2B, SPacketSvRPCExecute.class),
			define_SERVER_(0x2C, SPacketSvRPCExecuteVoid.class),
			define_CLIENT_(0x2D, CPacketSvRPCResultSuccess.class),
			define_CLIENT_(0x2E, CPacketSvRPCResultFail.class),
			define_SERVER_(0x2F, SPacketSvRPCResultSuccess.class),
			define_SERVER_(0x30, SPacketSvRPCResultFail.class),
			define_SERVER_(0x31, SPacketSvRPCResultMulti.class)
	);

	public static final int CLIENT_TO_SERVER = 0;
	public static final int SERVER_TO_CLIENT = 1;

	public static String dirStr(int dir) {
		return (dir == 0) ? "CLIENT_TO_SERVER" : ((dir == 1) ? "SERVER_TO_CLIENT" : "UNKNOWN");
	}

	public final int vers;

	private final PacketDef[] idMap = new PacketDef[64]; // May need to grow this in the future
	private final Map<Class<? extends EaglerSupervisorPacket>, PacketDef> classMap = new HashMap<>();

	private EaglerSupervisorProtocol(int vers, PacketDef...pkts) {
		this.vers = vers;
		for(int i = 0; i < pkts.length; ++i) {
			PacketDef def = pkts[i];
			if(idMap[def.id] != null) {
				throw new IllegalArgumentException("Packet ID " + def.id + " registered twice!");
			}
			idMap[def.id] = def;
			if(classMap.put(def.pkt, def) != null) {
				throw new IllegalArgumentException("Packet class " + def.pkt.getSimpleName() + " registered twice!");
			}
		}
	}

	private static PacketDef define_CLIENT_(int id, Class<? extends EaglerSupervisorPacket> pkt) {
		return new PacketDef(id, 0, pkt);
	}

	private static PacketDef define_SERVER_(int id, Class<? extends EaglerSupervisorPacket> pkt) {
		return new PacketDef(id, 1, pkt);
	}

	private static class PacketDef {

		private final int id;
		private final int dir;
		private final Class<? extends EaglerSupervisorPacket> pkt;
		private final Constructor<? extends EaglerSupervisorPacket> ctor;

		private PacketDef(int id, int dir, Class<? extends EaglerSupervisorPacket> pkt) {
			this.id = id;
			this.dir = dir;
			this.pkt = pkt;
			try {
				this.ctor = pkt.getConstructor();
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}

	}

	public int getPacketID(Class<? extends EaglerSupervisorPacket> pkt, int dir) {
		PacketDef def = classMap.get(pkt);
		if(def != null && def.dir == dir) {
			return def.id;
		}else {
			return -1;
		}
	}

	public EaglerSupervisorPacket createPacket(int id, int dir) {
		PacketDef def;
		if(id >= 0 && id < idMap.length && (def = idMap[id]) != null && def.dir == dir) {
			try {
				return def.ctor.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}else {
			return null;
		}
	}

}