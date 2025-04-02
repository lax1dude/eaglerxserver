package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCDisplayWebViewURLV2 implements EaglerBackendRPCPacket {

	public static final int FLAG_PERMS_JAVASCRIPT = 1;
	public static final int FLAG_PERMS_MESSAGE_API = 2;
	public static final int FLAG_PERMS_STRICT_CSP = 4;

	public int flags;
	public String embedTitle;
	public String embedURL;

	public CPacketRPCDisplayWebViewURLV2() {
	}

	public CPacketRPCDisplayWebViewURLV2(int flags, String embedTitle, String embedURL) {
		this.flags = flags;
		this.embedTitle = embedTitle;
		this.embedURL = embedURL;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		flags = buffer.readUnsignedByte();
		embedTitle = EaglerBackendRPCPacket.readString(buffer, 255, true, StandardCharsets.UTF_8);
		embedURL = EaglerBackendRPCPacket.readString(buffer, 65535, true, StandardCharsets.US_ASCII);
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeByte(flags);
		EaglerBackendRPCPacket.writeString(buffer, embedTitle, true, StandardCharsets.UTF_8);
		EaglerBackendRPCPacket.writeString(buffer, embedURL, true, StandardCharsets.US_ASCII);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return -1;
	}

}
