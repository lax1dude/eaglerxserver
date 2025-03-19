package net.lax1dude.eaglercraft.backend.server.base.message;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.voice.VoiceManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.*;

public class ServerV3MessageHandler extends ServerMessageHandler {

	public ServerV3MessageHandler(EaglerPlayerInstance<?> eaglerHandle) {
		super(eaglerHandle);
	}

	public void handleClient(CPacketGetOtherCapeEAG packet) {
		eaglerHandle.getSkinManager().handlePacketGetOtherCape(packet.uuidMost, packet.uuidLeast);
	}

	public void handleClient(CPacketGetOtherSkinEAG packet) {
		eaglerHandle.getSkinManager().handlePacketGetOtherSkin(packet.uuidMost, packet.uuidLeast);
	}

	public void handleClient(CPacketGetSkinByURLEAG packet) {
		eaglerHandle.getSkinManager().handlePacketGetSkinByURL(packet.uuidMost, packet.uuidLeast, packet.url);
	}

	public void handleClient(CPacketVoiceSignalConnectEAG packet) {
		VoiceManager<?> mgr = eaglerHandle.getVoiceManagerOrNull();
		if(mgr != null) {
			mgr.handleVoiceSignalPacketTypeConnect();
		}else {
			throw notCapable();
		}
	}

	public void handleClient(CPacketVoiceSignalDescEAG packet) {
		VoiceManager<?> mgr = eaglerHandle.getVoiceManagerOrNull();
		if(mgr != null) {
			mgr.handleVoiceSignalPacketTypeDesc(new UUID(packet.uuidMost, packet.uuidLeast), packet.desc);
		}else {
			throw notCapable();
		}
	}

	public void handleClient(CPacketVoiceSignalDisconnectV3EAG packet) {
		VoiceManager<?> mgr = eaglerHandle.getVoiceManagerOrNull();
		if(mgr != null) {
			if(packet.isPeerType) {
				mgr.handleVoiceSignalPacketTypeDisconnectPeer(new UUID(packet.uuidMost, packet.uuidLeast));
			}else {
				mgr.handleVoiceSignalPacketTypeDisconnect();
			}
		}else {
			throw notCapable();
		}
	}

	public void handleClient(CPacketVoiceSignalICEEAG packet) {
		VoiceManager<?> mgr = eaglerHandle.getVoiceManagerOrNull();
		if(mgr != null) {
			mgr.handleVoiceSignalPacketTypeICE(new UUID(packet.uuidMost, packet.uuidLeast), packet.ice);
		}else {
			throw notCapable();
		}
	}

	public void handleClient(CPacketVoiceSignalRequestEAG packet) {
		VoiceManager<?> mgr = eaglerHandle.getVoiceManagerOrNull();
		if(mgr != null) {
			mgr.handleVoiceSignalPacketTypeRequest(new UUID(packet.uuidMost, packet.uuidLeast));
		}else {
			throw notCapable();
		}
	}

}
