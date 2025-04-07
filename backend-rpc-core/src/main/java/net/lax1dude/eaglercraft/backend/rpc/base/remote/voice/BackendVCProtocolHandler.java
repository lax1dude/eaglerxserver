package net.lax1dude.eaglercraft.backend.rpc.base.remote.voice;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.WrongVCPacketException;

public abstract class BackendVCProtocolHandler implements EaglerVCHandler {

	protected final VoiceManagerRemote<?> voiceManager;

	public BackendVCProtocolHandler(VoiceManagerRemote<?> voiceManager) {
		this.voiceManager = voiceManager;
	}

	protected RuntimeException wrongPacket() {
		return new WrongVCPacketException();
	}

}
