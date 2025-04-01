package net.lax1dude.eaglercraft.backend.voice.protocol.pkt;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.*;

public interface EaglerVCHandler {

	default void handleClient(CPacketVCCapable packet) {
		throw new WrongVCPacketException();
	}

	default void handleClient(CPacketVCConnect packet) {
		throw new WrongVCPacketException();
	}

	default void handleClient(CPacketVCConnectPeer packet) {
		throw new WrongVCPacketException();
	}

	default void handleClient(CPacketVCDisconnect packet) {
		throw new WrongVCPacketException();
	}

	default void handleClient(CPacketVCDisconnectPeer packet) {
		throw new WrongVCPacketException();
	}

	default void handleClient(CPacketVCDescription packet) {
		throw new WrongVCPacketException();
	}

	default void handleClient(CPacketVCICECandidate packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCCapable packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCAllowed packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCPlayerList packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCAnnounce packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCConnectPeer packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCDisconnectPeer packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCDescription packet) {
		throw new WrongVCPacketException();
	}

	default void handleServer(SPacketVCICECandidate packet) {
		throw new WrongVCPacketException();
	}

}
