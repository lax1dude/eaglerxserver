package net.lax1dude.eaglercraft.backend.voice.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.*;

public enum EaglerVCProtocol {
	INIT(0,
			define_CLIENT_(0x00, CPacketVCCapable.class),
			define_SERVER_(0x01, SPacketVCCapable.class)
	), V1(1,
			// client-to-server
			define_CLIENT_(0x01, CPacketVCConnect.class),
			define_CLIENT_(0x02, CPacketVCConnectPeer.class),
			define_CLIENT_(0x03, CPacketVCDisconnect.class),
			define_CLIENT_(0x04, CPacketVCDisconnectPeer.class),
			define_CLIENT_(0x05, CPacketVCDescription.class),
			define_CLIENT_(0x06, CPacketVCICECandidate.class),

			// server-to-client
			define_SERVER_(0x01, SPacketVCAllowed.class),
			define_SERVER_(0x02, SPacketVCPlayerList.class),
			define_SERVER_(0x03, SPacketVCAnnounce.class),
			define_SERVER_(0x04, SPacketVCConnectPeer.class),
			define_SERVER_(0x05, SPacketVCDisconnectPeer.class),
			define_SERVER_(0x06, SPacketVCDescription.class),
			define_SERVER_(0x07, SPacketVCICECandidate.class)
	);

	public static final String CHANNEL_NAME = "EAG|1.8-Voice-RPC";
	public static final String CHANNEL_NAME_MODERN = "eagler:1-8-voice-rpc";

	public static final int CLIENT_TO_SERVER = 0;
	public static final int SERVER_TO_CLIENT = 1;

	public final int vers;

	private final PacketDef[] idMap = new PacketDef[32]; // May need to grow this in the future
	private final Map<Class<? extends EaglerVCPacket>, PacketDef> classMap = new HashMap<>();

	private EaglerVCProtocol(int vers, PacketDef...pkts) {
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

	private static PacketDef define_CLIENT_(int id, Class<? extends EaglerVCPacket> pkt) {
		return new PacketDef(id, CLIENT_TO_SERVER, pkt);
	}

	private static PacketDef define_SERVER_(int id, Class<? extends EaglerVCPacket> pkt) {
		return new PacketDef(id, SERVER_TO_CLIENT, pkt);
	}

	private static class PacketDef {

		private final int id;
		private final int dir;
		private final Class<? extends EaglerVCPacket> pkt;
		private final Constructor<? extends EaglerVCPacket> ctor;

		private PacketDef(int id, int dir, Class<? extends EaglerVCPacket> pkt) {
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

	public EaglerVCPacket readPacket(DataInput buffer, int dir) throws IOException {
		int pktId = buffer.readUnsignedByte();
		if(pktId >= idMap.length) {
			throw new IOException("Packet ID is out of range: 0x" + Integer.toHexString(pktId));
		}
		PacketDef pp = idMap[pktId];
		if(pp == null || pp.dir != dir) {
			throw new IOException("Unknown packet ID: 0x" + Integer.toHexString(pktId));
		}
		EaglerVCPacket newPkt;
		try {
			newPkt = pp.ctor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		newPkt.readPacket(buffer);
		return newPkt;
	}

	public void writePacket(DataOutput buffer, int dir, EaglerVCPacket packet) throws IOException {
		Class<? extends EaglerVCPacket> clazz = packet.getClass();
		PacketDef def = classMap.get(clazz);
		if(def == null || def.dir != dir) {
			throw new IOException("Unknown packet type or wrong direction: " + clazz);
		}
		buffer.writeByte(def.id);
		packet.writePacket(buffer);
	}

	public static EaglerVCProtocol getByID(int id) {
		switch(id) {
		case 0: return INIT;
		case 1: return V1;
		default: return null;
		}
	}

}
