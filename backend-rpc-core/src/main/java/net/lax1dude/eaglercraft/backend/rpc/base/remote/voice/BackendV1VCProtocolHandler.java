package net.lax1dude.eaglercraft.backend.rpc.base.remote.voice;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.*;

public class BackendV1VCProtocolHandler extends BackendVCProtocolHandler {

	public BackendV1VCProtocolHandler(VoiceManagerRemote<?> voiceManager) {
		super(voiceManager);
	}

	public void handleClient(CPacketVCConnect packet) {
		voiceManager.handlePlayerSignalPacketTypeConnect();
	}

	public void handleClient(CPacketVCConnectPeer packet) {
		voiceManager.handlePlayerSignalPacketTypeRequest(packet.uuidMost, packet.uuidLeast);
	}

	public void handleClient(CPacketVCDisconnect packet) {
		voiceManager.handlePlayerSignalPacketTypeDisconnect();
	}

	public void handleClient(CPacketVCDisconnectPeer packet) {
		voiceManager.handlePlayerSignalPacketTypeDisconnectPeer(packet.uuidMost, packet.uuidLeast);
	}

	public void handleClient(CPacketVCDescription packet) {
		voiceManager.handlePlayerSignalPacketTypeDesc(packet.uuidMost, packet.uuidLeast, packet.desc);
	}

	public void handleClient(CPacketVCICECandidate packet) {
		voiceManager.handlePlayerSignalPacketTypeICE(packet.uuidMost, packet.uuidLeast, packet.ice);
	}

}
