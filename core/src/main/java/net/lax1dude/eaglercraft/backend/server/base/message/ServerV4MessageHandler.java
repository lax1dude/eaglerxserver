package net.lax1dude.eaglercraft.backend.server.base.message;

import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.WrongPacketException;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.*;

public class ServerV4MessageHandler extends ServerV3MessageHandler {

	public ServerV4MessageHandler(EaglerPlayerInstance<?> eaglerHandle) {
		super(eaglerHandle);
	}

	public void handleClient(CPacketVoiceSignalDisconnectV3EAG packet) {
		throw new WrongPacketException();
	}

	public void handleClient(CPacketVoiceSignalDisconnectV4EAG packet) {
		
	}

	public void handleClient(CPacketVoiceSignalDisconnectPeerV4EAG packet) {
		
	}

	public void handleClient(CPacketGetOtherClientUUIDV4EAG packet) {
		eaglerHandle.handlePacketGetOtherClientUUID(packet.playerUUIDMost, packet.playerUUIDLeast, packet.requestId);
	}

	public void handleClient(CPacketRequestServerInfoV4EAG packet) {
		
	}

	public void handleClient(CPacketWebViewMessageV4EAG packet) {
		
	}

	public void handleClient(CPacketWebViewMessageEnV4EAG packet) {
		
	}

}
