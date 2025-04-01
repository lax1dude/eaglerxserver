package net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCHandler;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;

public class CPacketVCCapable implements EaglerVCPacket {

	public int[] versions;

	public CPacketVCCapable() {
	}

	public CPacketVCCapable(int[] versions) {
		this.versions = versions;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		int cnt = buffer.readUnsignedByte();
		if(cnt > 0) {
			versions = new int[cnt];
			for(int i = 0; i < cnt; ++i) {
				versions[i] = buffer.readUnsignedByte();
			}
		}else {
			versions = null;
		}
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		if(versions != null) {
			int cnt = versions.length;
			buffer.writeByte(cnt);
			for(int i = 0; i < cnt; ++i) {
				buffer.writeByte(versions[i]);
			}
		}else {
			buffer.writeByte(0);
		}
	}

	@Override
	public void handlePacket(EaglerVCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		int i = 1;
		if(versions != null) i += versions.length;
		return i;
	}

}
