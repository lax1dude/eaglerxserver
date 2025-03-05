package net.lax1dude.eaglercraft.backend.server.base.skins;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinCustomV3EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinCustomV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.SkinPacketVersionCache;

public class EaglerPlayerSkin implements IEaglerPlayerSkin {

	private final SkinPacketVersionCache skin;

	public EaglerPlayerSkin(SkinPacketVersionCache skin) {
		this.skin = skin;
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		return skin.get(protocol, rewriteUUIDMost, rewriteUUIDLeast);
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, EnumSkinModel rewriteModelId,
			GamePluginMessageProtocol protocol) {
		return skin.get(protocol, rewriteUUIDMost, rewriteUUIDLeast, rewriteModelId.getId());
	}

	@Override
	public GameMessagePacket getForceSkinPacketV4() {
		return skin.getForceClientV4();
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public boolean isSkinPreset() {
		return skin.isPreset();
	}

	@Override
	public int getPresetSkinId() {
		int id = skin.getPresetId();
		if(id != -1) {
			return id;
		}else {
			throw new UnsupportedOperationException("EaglerPlayerSkin is not a preset skin");
		}
	}

	@Override
	public EnumPresetSkins getPresetSkin() {
		int id = skin.getPresetId();
		if(id != -1) {
			return EnumPresetSkins.getById(id);
		}else {
			throw new UnsupportedOperationException("EaglerPlayerSkin is not a preset skin");
		}
	}

	@Override
	public boolean isSkinCustom() {
		return !skin.isPreset();
	}

	@Override
	public void getCustomSkinPixels_RGBA8_64x64(byte[] array, int offset) {
		GameMessagePacket pkt = skin.getV3();
		if(pkt instanceof SPacketOtherSkinCustomV3EAG) {
			System.arraycopy(((SPacketOtherSkinCustomV3EAG) pkt).customSkin, 0, array, offset, 16384);
		}else {
			throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
		}
	}

	@Override
	public void getCustomSkinPixels_eagler(byte[] array, int offset) {
		GameMessagePacket pkt = skin.getV4();
		if(pkt instanceof SPacketOtherSkinCustomV4EAG) {
			System.arraycopy(((SPacketOtherSkinCustomV4EAG) pkt).customSkin, 0, array, offset, 12288);
		}else {
			throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
		}
	}

	@Override
	public EnumSkinModel getCustomSkinModelId() {
		int id = skin.getModelId();
		if(id != -1) {
			return EnumSkinModel.getById(id);
		}else {
			throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
		}
	}

}
