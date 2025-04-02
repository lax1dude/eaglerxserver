package net.lax1dude.eaglercraft.backend.server.base.voice;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCAllowed;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCAnnounce;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCConnectPeer;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCDescription;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCDisconnectPeer;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCICECandidate;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCPlayerList;

public class ServerV1VCProtocolHandler extends ServerVCProtocolHandler {

	public ServerV1VCProtocolHandler(VoiceManagerRemote<?> voiceManager, String[] iceServerStash,
			boolean iceServerOverride) {
		super(voiceManager, iceServerStash, iceServerOverride);
	}

	public void handleServer(SPacketVCAllowed packet) {
		voiceManager.handleBackendSignalPacketAllowed(packet.allowed);
	}

	public void handleServer(SPacketVCPlayerList packet) {
		voiceManager.handleBackendSignalPacketPlayerList(packet.users);
	}

	public void handleServer(SPacketVCAnnounce packet) {
		voiceManager.handleBackendSignalPacketAnnounce(packet.uuidMost, packet.uuidLeast);
	}

	public void handleServer(SPacketVCConnectPeer packet) {
		voiceManager.handleBackendSignalPacketConnectPeer(packet.uuidMost, packet.uuidLeast, packet.offer);
	}

	public void handleServer(SPacketVCDisconnectPeer packet) {
		voiceManager.handleBackendSignalPacketDisconnectPeer(packet.uuidMost, packet.uuidLeast);
	}

	public void handleServer(SPacketVCDescription packet) {
		voiceManager.handleBackendSignalPacketDescription(packet.uuidMost, packet.uuidLeast, packet.desc);
	}

	public void handleServer(SPacketVCICECandidate packet) {
		voiceManager.handleBackendSignalPacketICECandidate(packet.uuidMost, packet.uuidLeast, packet.ice);
	}

}
