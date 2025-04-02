package net.lax1dude.eaglercraft.backend.server.base.message;

import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.voice.IVoiceManagerImpl;
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
		IVoiceManagerImpl<?> mgr = eaglerHandle.getVoiceManager();
		if(mgr != null) {
			mgr.handlePlayerSignalPacketTypeConnect();
		}
		// Do not throw "not capable" on connect, clients may join in a bad state and send it mistakenly
	}

	public void handleClient(CPacketVoiceSignalDescEAG packet) {
		IVoiceManagerImpl<?> mgr = eaglerHandle.getVoiceManager();
		if(mgr != null) {
			mgr.handlePlayerSignalPacketTypeDesc(packet.uuidMost, packet.uuidLeast, packet.desc);
		}else {
			throw notCapable();
		}
	}

	public void handleClient(CPacketVoiceSignalDisconnectV3EAG packet) {
		IVoiceManagerImpl<?> mgr = eaglerHandle.getVoiceManager();
		if(mgr != null) {
			if(packet.isPeerType) {
				mgr.handlePlayerSignalPacketTypeDisconnectPeer(packet.uuidMost, packet.uuidLeast);
			}else {
				mgr.handlePlayerSignalPacketTypeDisconnect();
			}
		}else {
			throw notCapable();
		}
	}

	public void handleClient(CPacketVoiceSignalICEEAG packet) {
		IVoiceManagerImpl<?> mgr = eaglerHandle.getVoiceManager();
		if(mgr != null) {
			mgr.handlePlayerSignalPacketTypeICE(packet.uuidMost, packet.uuidLeast, packet.ice);
		}else {
			throw notCapable();
		}
	}

	public void handleClient(CPacketVoiceSignalRequestEAG packet) {
		IVoiceManagerImpl<?> mgr = eaglerHandle.getVoiceManager();
		if(mgr != null) {
			mgr.handlePlayerSignalPacketTypeRequest(packet.uuidMost, packet.uuidLeast);
		}else {
			throw notCapable();
		}
	}

}
