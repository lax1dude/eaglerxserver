package net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCHandler;
import net.lax1dude.eaglercraft.backend.rpc.protocol.pkt.EaglerBackendRPCPacket;

public class CPacketRPCSetPlayerTexturesPresetV2 implements EaglerBackendRPCPacket {

	public boolean notifyOthers;
	public int presetSkinId;
	public int presetCapeId;

	public CPacketRPCSetPlayerTexturesPresetV2() {
	}

	public CPacketRPCSetPlayerTexturesPresetV2(boolean notifyOthers, int presetSkinId, int presetCapeId) {
		this.notifyOthers = notifyOthers;
		this.presetSkinId = presetSkinId;
		this.presetCapeId = presetCapeId;
	}

	@Override
	public void readPacket(DataInput buffer) throws IOException {
		notifyOthers = buffer.readBoolean();
		presetSkinId = buffer.readInt();
		presetCapeId = buffer.readInt();
	}

	@Override
	public void writePacket(DataOutput buffer) throws IOException {
		buffer.writeBoolean(notifyOthers);
		buffer.writeInt(presetSkinId);
		buffer.writeInt(presetCapeId);
	}

	@Override
	public void handlePacket(EaglerBackendRPCHandler handler) {
		handler.handleClient(this);
	}

	@Override
	public int length() {
		return 9;
	}

}
