package net.lax1dude.eaglercraft.backend.server.base.skins.type;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.server.base.skins.SkinConverterExt;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientCapeCustomV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapeCustomEAG;

public class CustomCapePlayer extends BaseCustomCape {

	private final SPacketOtherCapeCustomEAG packet;

	public CustomCapePlayer(long uuidMost, long uuidLeast, byte[] textureData) {
		packet = new SPacketOtherCapeCustomEAG(uuidMost, uuidLeast, textureData);
	}

	public CustomCapePlayer(SPacketOtherCapeCustomEAG pkt) {
		packet = pkt;
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
			return new SPacketOtherCapeCustomEAG(rewriteUUIDMost, rewriteUUIDLeast, packet.customCape);
		}
	}

	@Override
	public GameMessagePacket getForceCapePacketV4() {
		return new SPacketForceClientCapeCustomV4EAG(packet.customCape);
	}

	@Override
	public boolean isCapeEnabled() {
		return true;
	}

	@Override
	public boolean isCapePreset() {
		return false;
	}

	@Override
	public int getPresetCapeId() {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a preset cape");
	}

	@Override
	public EnumPresetCapes getPresetCape() {
		throw new UnsupportedOperationException("EaglerPlayerCape is not a preset cape");
	}

	@Override
	public boolean isCapeCustom() {
		return true;
	}

	@Override
	public void getCustomCapePixels_ABGR8_32x32(byte[] array, int offset) {
		SkinConverterExt.convertCape23x17RGBto32x32ABGR(packet.customCape, 0, array, offset);
	}

	@Override
	public void getCustomCapePixels_eagler(byte[] array, int offset) {
		System.arraycopy(packet.customCape, 0, array, offset, 1173);
	}

	@Override
	protected byte[] textureData() {
		return packet.customCape;
	}

}
