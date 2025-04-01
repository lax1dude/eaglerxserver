package net.lax1dude.eaglercraft.backend.voice.protocol.pkt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface EaglerVCPacket {

	void readPacket(DataInput buffer) throws IOException;

	void writePacket(DataOutput buffer) throws IOException;

	void handlePacket(EaglerVCHandler handler);

	int length();

	public static void writeString(DataOutput buffer, String str, boolean len16, Charset charset) throws IOException {
		if(str == null || str.length() == 0) {
			if(len16) {
				buffer.writeShort(0);
			}else {
				buffer.writeByte(0);
			}
			return;
		}
		byte[] bytes = str.getBytes(charset);
		if(bytes.length > (len16 ? 65535 : 255)) {
			throw new IOException("String is too long!");
		}
		if(len16) {
			buffer.writeShort(bytes.length);
		}else {
			buffer.writeByte(bytes.length);
		}
		buffer.write(bytes);
	}

	public static String readString(DataInput buffer, int maxLen, boolean len16, Charset charset) throws IOException {
		int len = len16 ? buffer.readUnsignedShort() : buffer.readUnsignedByte();
		if(len > maxLen) {
			throw new IOException("String is too long!");
		}
		if(len == 0) {
			return "";
		}
		byte[] toRead = new byte[len];
		buffer.readFully(toRead);
		String ret = new String(toRead, charset);
		if(charset != StandardCharsets.US_ASCII && ret.length() > maxLen) {
			throw new IOException("String is too long!");
		}
		return ret;
	}

}
