package net.lax1dude.eaglercraft.backend.server.base.message;

import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
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
		
	}

	public void handleClient(CPacketVoiceSignalDescEAG packet) {
		
	}

	public void handleClient(CPacketVoiceSignalDisconnectV3EAG packet) {
		
	}

	public void handleClient(CPacketVoiceSignalICEEAG packet) {
		
	}

	public void handleClient(CPacketVoiceSignalRequestEAG packet) {
		
	}

}
