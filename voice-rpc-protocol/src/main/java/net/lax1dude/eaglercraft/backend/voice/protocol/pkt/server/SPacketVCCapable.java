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
	public String[] iceServers;

	public SPacketVCCapable() {
	}

	public SPacketVCCapable(int version, boolean allowed, String[] iceServers) {
		this.version = version;
		this.allowed = allowed;
		this.iceServers = iceServers;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		version = buffer.readUnsignedByte();
		int numIce = buffer.readUnsignedByte();
		if(numIce > 0) {
			allowed = true;
			if(numIce > 64) {
				throw new IOException("Too many STUN/TURN servers recieved! (" + numIce + ", max is 64!)");
			}
			iceServers = new String[numIce];
			for(int i = 0; i < iceServers.length; ++i) {
				iceServers[i] = EaglerVCPacket.readString(buffer, 1024, true, StandardCharsets.UTF_8);
			}
		}else {
			allowed = false;
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeByte(version);
		if(allowed) {
			int cnt;
			if(iceServers != null && (cnt = iceServers.length) > 0) {
				if(cnt > 64) {
					throw new IOException("Too many STUN/TURN servers sent! (" + cnt + ", max is 64!)");
				}
				buffer.writeByte(cnt);
				for(int i = 0; i < cnt; ++i) {
					EaglerVCPacket.writeString(buffer, iceServers[i], true, StandardCharsets.UTF_8);
				}
			}else {
				throw new IOException("No STUN/TURN servers provided");
			}
		}else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(EaglerVCHandler handler) {
		handler.handleServer(this);
	}

	@Override
	public int length() {
		return allowed ? -1 : 2;
	}

}
