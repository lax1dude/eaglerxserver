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

package net.lax1dude.eaglercraft.backend.rpc.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.server.*;

public enum EaglerBackendRPCProtocol {
	INIT(0,
			define_CLIENT_(0x00, CPacketRPCEnabled.class),
			define_SERVER_(0x01, SPacketRPCEnabledSuccess.class),
			define_SERVER_(0x02, SPacketRPCEnabledFailure.class),
			define_SERVER_(0x03, SPacketRPCEnabledSuccessVanillaV2.class),
			define_SERVER_(0x04, SPacketRPCEnabledSuccessEaglerV2.class)
	), V1(1,
			define_CLIENT_(0x03, CPacketRPCDisabled.class),
			define_CLIENT_(0x04, CPacketRPCRequestPlayerInfo.class),
			define_CLIENT_(0x05, CPacketRPCSubscribeEvents.class),
			define_CLIENT_(0x06, CPacketRPCSetPlayerSkin.class),
			define_CLIENT_(0x07, CPacketRPCSetPlayerCape.class),
			define_CLIENT_(0x08, CPacketRPCSetPlayerCookie.class),
			define_CLIENT_(0x09, CPacketRPCSetPlayerFNAWEn.class),
			define_CLIENT_(0x0A, CPacketRPCSetPauseMenuCustom.class),
			define_CLIENT_(0x0B, CPacketRPCRedirectPlayer.class),
			define_CLIENT_(0x0C, CPacketRPCResetPlayerMulti.class),
			define_SERVER_(0x0D, SPacketRPCResponseTypeNull.class),
			define_SERVER_(0x0E, SPacketRPCResponseTypeBytes.class),
			define_SERVER_(0x0F, SPacketRPCResponseTypeString.class),
			define_SERVER_(0x10, SPacketRPCResponseTypeUUID.class),
			define_SERVER_(0x11, SPacketRPCResponseTypeCookie.class),
			define_SERVER_(0x12, SPacketRPCResponseTypeVoiceStatus.class),
			define_SERVER_(0x13, SPacketRPCResponseTypeWebViewStatus.class),
			define_SERVER_(0x14, SPacketRPCResponseTypeError.class),
			define_CLIENT_(0x15, CPacketRPCSendWebViewMessage.class),
			define_SERVER_(0x16, SPacketRPCEventWebViewOpenClose.class),
			define_SERVER_(0x17, SPacketRPCEventWebViewMessage.class),
			define_SERVER_(0x18, SPacketRPCEventToggledVoice.class),
			define_CLIENT_(0x19, CPacketRPCNotifIconRegister.class),
			define_CLIENT_(0x1A, CPacketRPCNotifIconRelease.class),
			define_CLIENT_(0x1B, CPacketRPCNotifBadgeShow.class),
			define_CLIENT_(0x1C, CPacketRPCNotifBadgeHide.class),
			define_CLIENT_(0x1D, CPacketRPCSendRawMessage.class)
	), V2(2,
			// client-to-server
			define_CLIENT_(0x01, CPacketRPCDisabled.class),
			define_CLIENT_(0x02, CPacketRPCRequestPlayerInfo.class),
			define_CLIENT_(0x03, CPacketRPCSubscribeEvents.class),
			define_CLIENT_(0x04, CPacketRPCSetPlayerSkin.class),
			define_CLIENT_(0x05, CPacketRPCSetPlayerSkinPresetV2.class),
			define_CLIENT_(0x06, CPacketRPCSetPlayerCape.class),
			define_CLIENT_(0x07, CPacketRPCSetPlayerCapePresetV2.class),
			define_CLIENT_(0x08, CPacketRPCSetPlayerTexturesV2.class),
			define_CLIENT_(0x09, CPacketRPCSetPlayerTexturesPresetV2.class),
			define_CLIENT_(0x0A, CPacketRPCSetPlayerCookie.class),
			define_CLIENT_(0x0B, CPacketRPCSetPlayerFNAWEn.class),
			define_CLIENT_(0x0C, CPacketRPCSetPauseMenuCustom.class),
			define_CLIENT_(0x0D, CPacketRPCRedirectPlayer.class),
			define_CLIENT_(0x0E, CPacketRPCResetPlayerMulti.class),
			define_CLIENT_(0x0F, CPacketRPCSendWebViewMessage.class),
			define_CLIENT_(0x10, CPacketRPCNotifIconRegister.class),
			define_CLIENT_(0x11, CPacketRPCNotifIconRelease.class),
			define_CLIENT_(0x12, CPacketRPCNotifBadgeShow.class),
			define_CLIENT_(0x13, CPacketRPCNotifBadgeHide.class),
			define_CLIENT_(0x14, CPacketRPCSendRawMessage.class),
			define_CLIENT_(0x15, CPacketRPCInjectRawBinaryFrameV2.class),
			
			// server-to-client
			define_SERVER_(0x01, SPacketRPCResponseTypeNull.class),
			define_SERVER_(0x02, SPacketRPCResponseTypeBytes.class),
			define_SERVER_(0x03, SPacketRPCResponseTypeIntegerSingleV2.class),
			define_SERVER_(0x04, SPacketRPCResponseTypeIntegerTupleV2.class),
			define_SERVER_(0x05, SPacketRPCResponseTypeString.class),
			define_SERVER_(0x06, SPacketRPCResponseTypeBrandDataV2.class),
			define_SERVER_(0x07, SPacketRPCResponseTypeUUID.class),
			define_SERVER_(0x08, SPacketRPCResponseTypeCookie.class),
			define_SERVER_(0x09, SPacketRPCResponseTypeVoiceStatus.class),
			define_SERVER_(0x0A, SPacketRPCResponseTypeWebViewStatusV2.class),
			define_SERVER_(0x0B, SPacketRPCResponseTypeError.class),
			define_SERVER_(0x0C, SPacketRPCEventWebViewOpenClose.class),
			define_SERVER_(0x0D, SPacketRPCEventWebViewMessage.class),
			define_SERVER_(0x0E, SPacketRPCEventToggledVoice.class)
	);

	public static final String CHANNEL_NAME = "EAG|1.8-RPC";
	public static final String CHANNEL_NAME_READY = "EAG|1.8-Ready";

	public static final String CHANNEL_NAME_MODERN = "eagler:1-8-rpc";
	public static final String CHANNEL_NAME_READY_MODERN = "eagler:1-8-ready";

	public static final int CLIENT_TO_SERVER = 0;
	public static final int SERVER_TO_CLIENT = 1;

	public final int vers;

	private final PacketDef[] idMap = new PacketDef[32]; // May need to grow this in the future
	private final Map<Class<? extends EaglerBackendRPCPacket>, PacketDef> classMap = new HashMap<>();

	private EaglerBackendRPCProtocol(int vers, PacketDef...pkts) {
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

	private static PacketDef define_CLIENT_(int id, Class<? extends EaglerBackendRPCPacket> pkt) {
		return new PacketDef(id, CLIENT_TO_SERVER, pkt);
	}

	private static PacketDef define_SERVER_(int id, Class<? extends EaglerBackendRPCPacket> pkt) {
		return new PacketDef(id, SERVER_TO_CLIENT, pkt);
	}

	private static class PacketDef {

		private final int id;
		private final int dir;
		private final Class<? extends EaglerBackendRPCPacket> pkt;
		private final Constructor<? extends EaglerBackendRPCPacket> ctor;

		private PacketDef(int id, int dir, Class<? extends EaglerBackendRPCPacket> pkt) {
			this.id = id;
			this.dir = dir;
			this.pkt = pkt;
			try {
				this.ctor = pkt.getConstructor();
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Packet does not have a default constructor: " + pkt.getName(), e);
			}
		}

	}

	public EaglerBackendRPCPacket readPacket(DataInput buffer, int dir) throws IOException {
		int pktId = buffer.readUnsignedByte();
		if(pktId >= idMap.length) {
			throw new IOException("Packet ID is out of range: 0x" + Integer.toHexString(pktId));
		}
		PacketDef pp = idMap[pktId];
		if(pp == null || pp.dir != dir) {
			throw new IOException("Unknown packet ID: 0x" + Integer.toHexString(pktId));
		}
		EaglerBackendRPCPacket newPkt;
		try {
			newPkt = pp.ctor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		newPkt.readPacket(buffer);
		return newPkt;
	}

	public void writePacket(DataOutput buffer, int dir, EaglerBackendRPCPacket packet) throws IOException {
		Class<? extends EaglerBackendRPCPacket> clazz = packet.getClass();
		PacketDef def = classMap.get(clazz);
		if(def == null || def.dir != dir) {
			throw new IOException("Unknown packet type or wrong direction: " + clazz);
		}
		buffer.writeByte(def.id);
		packet.writePacket(buffer);
	}

	public static EaglerBackendRPCProtocol getByID(int id) {
		switch(id) {
		case 0: return INIT;
		case 1: return V1;
		case 2: return V2;
		default: return null;
		}
	}

}