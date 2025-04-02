package net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;

public class SPacketVCCapable implements EaglerVCPacket {

	public int version;
	public boolean allowed;
	public boolean overrideICE;
	public String[] iceServers;

	public SPacketVCCapable() {
	}

	public SPacketVCCapable(int version, boolean allowed, boolean overrideICE, String[] iceServers) {
		this.version = version;
		this.allowed = allowed;
		this.overrideICE = overrideICE;
		this.iceServers = iceServers;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		version = buffer.readUnsignedByte();
		int numIce = buffer.readUnsignedByte();
		allowed = (numIce & 128) != 0;
		overrideICE = (numIce & 64) != 0;
		numIce &= 63;
		iceServers = new String[numIce];
		for(int i = 0; i < iceServers.length; ++i) {
			iceServers[i] = EaglerVCPacket.readString(buffer, 1024, true, StandardCharsets.UTF_8);
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		int j = 0;
		if(allowed) j |= 128;
		if(overrideICE) j |= 64;
		buffer.writeByte(version);
		int cnt;
		if(iceServers != null && (cnt = iceServers.length) > 0) {
			if(cnt > 63) {
				throw new IOException("Too many STUN/TURN servers sent! (" + cnt + ", max is 63!)");
			}
			buffer.writeByte(cnt | j);
			for(int i = 0; i < cnt; ++i) {
				EaglerVCPacket.writeString(buffer, iceServers[i], true, StandardCharsets.UTF_8);
			}
		}else {
			buffer.writeByte(j);
		}
	}

	@Override
	public void handlePacket(EaglerVCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return -1;
	}

}
