package net.lax1dude.eaglercraft.backend.server.base.skins.type;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientSkinCustomV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinCustomV3EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinCustomV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.SkinPacketVersionCache;

public class CustomSkinPlayer extends BaseCustomSkin implements IModelRewritable {

	private SPacketOtherSkinCustomV3EAG packetV3;
	private SPacketOtherSkinCustomV4EAG packetV4;

	private CustomSkinPlayer(SPacketOtherSkinCustomV3EAG packetV3, SPacketOtherSkinCustomV4EAG packetV4) {
		this.packetV3 = packetV3;
		this.packetV4 = packetV4;
	}

	public static CustomSkinPlayer createV3(long uuidMost, long uuidLeast, int modelId, byte[] textureDataV3) {
		return new CustomSkinPlayer(new SPacketOtherSkinCustomV3EAG(uuidMost, uuidLeast, modelId, textureDataV3), null);
	}

	public static CustomSkinPlayer createV4(long uuidMost, long uuidLeast, int modelId, byte[] textureDataV4) {
		return new CustomSkinPlayer(null, new SPacketOtherSkinCustomV4EAG(uuidMost, uuidLeast, modelId, textureDataV4));
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		switch(protocol) {
		case V3:
			SPacketOtherSkinCustomV3EAG packetV3 = packetV3();
			if(rewriteUUIDMost == packetV3.uuidMost && rewriteUUIDLeast == packetV3.uuidLeast) {
				return packetV3;
			}else {
				return new SPacketOtherSkinCustomV3EAG(rewriteUUIDMost, rewriteUUIDLeast, packetV3.modelID, packetV3.customSkin);
			}
		case V4:
		default:
			SPacketOtherSkinCustomV4EAG packetV4 = packetV4();
			if(rewriteUUIDMost == packetV4.uuidMost && rewriteUUIDLeast == packetV4.uuidLeast) {
				return packetV4;
			}else {
				return new SPacketOtherSkinCustomV4EAG(rewriteUUIDMost, rewriteUUIDLeast, packetV4.modelID, packetV4.customSkin);
			}
		}
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, EnumSkinModel rewriteModelId,
			GamePluginMessageProtocol protocol) {
		int modelIdInt = rewriteModelId.getId();
		switch(protocol) {
		case V3:
			SPacketOtherSkinCustomV3EAG packetV3 = packetV3();
			if(rewriteUUIDMost == packetV3.uuidMost && rewriteUUIDLeast == packetV3.uuidLeast && modelIdInt == packetV3.modelID) {
				return packetV3;
			}else {
				return new SPacketOtherSkinCustomV3EAG(rewriteUUIDMost, rewriteUUIDLeast, modelIdInt, packetV3.customSkin);
			}
		case V4:
		default:
			SPacketOtherSkinCustomV4EAG packetV4 = packetV4();
			if(rewriteUUIDMost == packetV4.uuidMost && rewriteUUIDLeast == packetV4.uuidLeast && modelIdInt == packetV4.modelID) {
				return packetV4;
			}else {
				return new SPacketOtherSkinCustomV4EAG(rewriteUUIDMost, rewriteUUIDLeast, modelIdInt, packetV4.customSkin);
			}
		}
	}

	@Override
	public GameMessagePacket getForceSkinPacketV4() {
		return new SPacketForceClientSkinCustomV4EAG(modelId(), textureDataV4());
	}

	@Override
	public boolean isSkinPreset() {
		return false;
	}

	@Override
	public int getPresetSkinId() {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a preset skin");
	}

	@Override
	public EnumPresetSkins getPresetSkin() {
		throw new UnsupportedOperationException("EaglerPlayerSkin is not a preset skin");
	}

	@Override
	public boolean isSkinCustom() {
		return true;
	}

	@Override
	public void getCustomSkinPixels_ABGR8_64x64(byte[] array, int offset) {
		System.arraycopy(textureDataV3(), 0, array, offset, 16384);
	}

	@Override
	public void getCustomSkinPixels_eagler(byte[] array, int offset) {
		System.arraycopy(textureDataV4(), 0, array, offset, 12288);
	}

	@Override
	public EnumSkinModel getCustomSkinModelId() {
		return EnumSkinModel.getById(modelId());
	}

	@Override
	protected int modelId() {
		if(packetV4 != null) {
			return packetV4.modelID;
		}else {
			return packetV3.modelID;
		}
	}

	protected SPacketOtherSkinCustomV3EAG packetV3() {
		if(packetV3 != null) {
			return packetV3;
		}else {
			return packetV3 = (SPacketOtherSkinCustomV3EAG) SkinPacketVersionCache.convertToV3(packetV4);
		}
	}

	protected SPacketOtherSkinCustomV4EAG packetV4() {
		if(packetV4 != null) {
			return packetV4;
		}else {
			return packetV4 = (SPacketOtherSkinCustomV4EAG) SkinPacketVersionCache.convertToV4(packetV3);
		}
	}

	@Override
	protected byte[] textureDataV3() {
		return packetV3().customSkin;
	}

	@Override
	protected byte[] textureDataV4() {
		return packetV4().customSkin;
	}

	@Override
	public IEaglerPlayerSkin rewriteModelInternal(int modelId) {
		if(modelId != modelId()) {
			return new CustomSkinModelRw(this, modelId);
		}else {
			return this;
		}
	}

}
