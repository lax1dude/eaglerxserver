package net.lax1dude.eaglercraft.backend.server.base.skins.type;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientCapePresetV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapePresetEAG;

public class PresetCapePlayer extends BasePresetCape {

	private final SPacketOtherCapePresetEAG packet;

	public PresetCapePlayer(long uuidMost, long uuidLeast, int presetId) {
		this.packet = new SPacketOtherCapePresetEAG(uuidMost, uuidLeast, presetId);
	}

	public PresetCapePlayer(SPacketOtherCapePresetEAG packet) {
		this.packet = packet;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public GameMessagePacket getCapePacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		if(rewriteUUIDMost == packet.uuidMost && rewriteUUIDLeast == packet.uuidLeast) {
			return packet;
		}else {
			return new SPacketOtherCapePresetEAG(rewriteUUIDMost, rewriteUUIDLeast, packet.presetCape);
		}
	}

	@Override
	public GameMessagePacket getForceCapePacketV4() {
		return new SPacketForceClientCapePresetV4EAG(packet.presetCape);
	}

	@Override
	public boolean isCapeEnabled() {
		return packet.presetCape != 0;
	}

	@Override
	public boolean isCapePreset() {
		return true;
	}

	@Override
	public int getPresetCapeId() {
		return packet.presetCape;
	}

	@Override
	public EnumPresetCapes getPresetCape() {
		return EnumPresetCapes.getByIdOrDefault(packet.presetCape);
	}

	@Override
	public boolean isCapeCustom() {
		return false;
	}

	@Override
	public void getCustomCapePixels_ABGR8_32x32(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a custom cape");
	}

	@Override
	public void getCustomCapePixels_eagler(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a custom cape");
	}

	@Override
	protected int presetId() {
		return packet.presetCape;
	}

}
