package net.lax1dude.eaglercraft.backend.server.base.skins.type;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientSkinPresetV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinPresetEAG;

public class MissingSkin extends BasePresetSkin {

	public static final IEaglerPlayerSkin MISSING_SKIN = new MissingSkin(EnumPresetSkins.DEFAULT_STEVE);
	public static final IEaglerPlayerSkin MISSING_SKIN_ALEX = new MissingSkin(EnumPresetSkins.DEFAULT_ALEX);

	// used for supervisor
	public static final IEaglerPlayerSkin UNAVAILABLE_SKIN = new MissingSkin(EnumPresetSkins.DEFAULT_STEVE);

	private final int skinId;
	private final EnumPresetSkins enumSkin;

	private MissingSkin(EnumPresetSkins enumSkin) {
		this.skinId = enumSkin.getId();
		this.enumSkin = enumSkin;
	}

	public static IEaglerPlayerSkin forPlayerUUID(UUID playerUUID) {
		return (playerUUID.hashCode() & 1) != 0 ? MISSING_SKIN_ALEX : MISSING_SKIN;
	}

	@Override
	public boolean isSuccess() {
		return false;
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, skinId);
	}

	@Override
	public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			EnumSkinModel rewriteModelId, GamePluginMessageProtocol protocol) {
		return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, skinId);
	}

	@Override
	public GameMessagePacket getForceSkinPacketV4() {
		return new SPacketForceClientSkinPresetV4EAG(0);
	}

	@Override
	public boolean isSkinPreset() {
		return true;
	}

	@Override
	public int getPresetSkinId() {
		return skinId;
	}

	@Override
	public EnumPresetSkins getPresetSkin() {
		return enumSkin;
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
		return skinId;
	}

}
