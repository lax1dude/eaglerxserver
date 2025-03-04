package net.lax1dude.eaglercraft.backend.server.base.skins;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientCapeCustomV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientCapePresetV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapeCustomEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapePresetEAG;

public class EaglerPlayerCape implements IEaglerPlayerCape {

	private final GameMessagePacket cape;

	public EaglerPlayerCape(GameMessagePacket cape) {
		this.cape = cape;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public GameMessagePacket getCapePacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		if(cape instanceof SPacketOtherCapePresetEAG) {
			SPacketOtherCapePresetEAG pkt = (SPacketOtherCapePresetEAG) cape;
			if(pkt.uuidMost != rewriteUUIDMost || pkt.uuidLeast != rewriteUUIDLeast) {
				return new SPacketOtherCapePresetEAG(rewriteUUIDMost, rewriteUUIDLeast, pkt.presetCape);
			}else {
				return pkt;
			}
		}else {
			SPacketOtherCapeCustomEAG pkt = (SPacketOtherCapeCustomEAG) cape;
			if(pkt.uuidMost != rewriteUUIDMost || pkt.uuidLeast != rewriteUUIDLeast) {
				return new SPacketOtherCapeCustomEAG(rewriteUUIDMost, rewriteUUIDLeast, pkt.customCape);
			}else {
				return pkt;
			}
		}
	}

	@Override
	public GameMessagePacket getForceCapePacketV4() {
		if(cape instanceof SPacketOtherCapePresetEAG) {
			return new SPacketForceClientCapePresetV4EAG(((SPacketOtherCapePresetEAG) cape).presetCape);
		}else {
			return new SPacketForceClientCapeCustomV4EAG(((SPacketOtherCapeCustomEAG) cape).customCape);
		}
	}

	@Override
	public boolean isCapeEnabled() {
		return !((cape instanceof SPacketOtherCapePresetEAG) && ((SPacketOtherCapePresetEAG)cape).presetCape == 0);
	}

	@Override
	public boolean isCapePreset() {
		return cape instanceof SPacketOtherCapePresetEAG;
	}

	@Override
	public int getPresetCapeId() {
		if(cape instanceof SPacketOtherCapePresetEAG) {
			return ((SPacketOtherCapePresetEAG)cape).presetCape;
		}else {
			throw new UnsupportedOperationException("EaglerPlayerCape is not a preset cape");
		}
	}

	@Override
	public EnumPresetCapes getPresetCape() {
		if(cape instanceof SPacketOtherCapePresetEAG) {
			return EnumPresetCapes.getById(((SPacketOtherCapePresetEAG)cape).presetCape);
		}else {
			throw new UnsupportedOperationException("EaglerPlayerCape is not a preset cape");
		}
	}

	@Override
	public boolean isCapeCustom() {
		return cape instanceof SPacketOtherCapeCustomEAG;
	}

	@Override
	public void getCustomCapePixels_RGBA8_32x32(byte[] array, int offset) {
		if(cape instanceof SPacketOtherCapeCustomEAG) {
			SkinConverterExt.convertCape23x17RGBto32x32RGBA(((SPacketOtherCapeCustomEAG)cape).customCape, 0, array, offset);
		}else {
			throw new UnsupportedOperationException("EaglerPlayerCape is not a custom cape");
		}
	}

	@Override
	public void getCustomCapePixels_eagler(byte[] array, int offset) {
		if(cape instanceof SPacketOtherCapeCustomEAG) {
			System.arraycopy(((SPacketOtherCapeCustomEAG)cape).customCape, 0, array, offset, 1173);
		}else {
			throw new UnsupportedOperationException("EaglerPlayerCape is not a custom cape");
		}
	}

}
