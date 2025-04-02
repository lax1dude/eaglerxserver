package net.lax1dude.eaglercraft.backend.server.base.voice;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.WrongVCPacketException;

public abstract class ServerVCProtocolHandler implements EaglerVCHandler {

	protected final VoiceManagerRemote<?> voiceManager;
	protected final String[] iceServerStash;
	protected final boolean iceServerOverride;

	public ServerVCProtocolHandler(VoiceManagerRemote<?> voiceManager, String[] iceServerStash,
			boolean iceServerOverride) {
		this.voiceManager = voiceManager;
		this.iceServerStash = iceServerStash;
		this.iceServerOverride = iceServerOverride;
	}

	protected RuntimeException wrongPacket() {
		return new WrongVCPacketException();
	}

}
