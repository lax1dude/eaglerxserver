package net.lax1dude.eaglercraft.backend.server.base.message;

import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.*;

public class ServerV3MessageHandler extends ServerMessageHandler {

	public ServerV3MessageHandler(EaglerPlayerInstance<?> eaglerHandle) {
		super(eaglerHandle);
	}

	public void handleClient(CPacketGetOtherCapeEAG packet) {
		eaglerHandle.getSkinManager().handlePacketGetOtherCape(packet);
	}

	public void handleClient(CPacketGetOtherSkinEAG packet) {
		eaglerHandle.getSkinManager().handlePacketGetOtherSkin(packet);
	}

	public void handleClient(CPacketGetSkinByURLEAG packet) {
		eaglerHandle.getSkinManager().handlePacketGetSkinByURL(packet);
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
