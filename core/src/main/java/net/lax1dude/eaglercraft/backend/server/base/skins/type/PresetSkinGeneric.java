package net.lax1dude.eaglercraft.backend.server.base.skins.type;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientSkinPresetV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinPresetEAG;

public class PresetSkinGeneric extends BasePresetSkin {

	private final int presetId;

	PresetSkinGeneric(int presetId) {
		this.presetId = presetId;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, presetId);
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, EnumSkinModel rewriteModelId,
			GamePluginMessageProtocol protocol) {
		return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, presetId);
	}

	@Override
	public GameMessagePacket getForceSkinPacketV4() {
		return new SPacketForceClientSkinPresetV4EAG(presetId);
	}

	@Override
	public boolean isSkinPreset() {
		return true;
	}

	@Override
	public int getPresetSkinId() {
		return presetId;
	}

	@Override
	public EnumPresetSkins getPresetSkin() {
		return EnumPresetSkins.getByIdOrDefault(presetId);
	}

	@Override
	public boolean isSkinCustom() {
		return false;
	}

	@Override
	public void getCustomSkinPixels_ABGR8_64x64(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
	}

	@Override
	public void getCustomSkinPixels_eagler(byte[] array, int offset) {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
	}

	@Override
	public EnumSkinModel getCustomSkinModelId() {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
	}

	@Override
	protected int presetId() {
		return presetId;
	}

}
