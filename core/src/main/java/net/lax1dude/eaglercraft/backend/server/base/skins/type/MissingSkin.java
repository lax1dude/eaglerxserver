package net.lax1dude.eaglercraft.backend.server.base.skins.type;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientSkinPresetV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherSkinPresetEAG;

public class MissingSkin {

	public static final IEaglerPlayerSkin MISSING_SKIN = new IEaglerPlayerSkin() {

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
				GamePluginMessageProtocol protocol) {
			return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, 0);
		}

		@Override
		public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
				EnumSkinModel rewriteModelId, GamePluginMessageProtocol protocol) {
			return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, 0);
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
			return 0;
		}

		@Override
		public EnumPresetSkins getPresetSkin() {
			return EnumPresetSkins.DEFAULT_STEVE;
		}

		@Override
		public boolean isSkinCustom() {
			return false;
		}

		@Override
		public void getCustomSkinPixels_RGBA8_64x64(byte[] array, int offset) {
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

	};

	public static final IEaglerPlayerSkin MISSING_SKIN_ALEX = new IEaglerPlayerSkin() {

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
				GamePluginMessageProtocol protocol) {
			return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, 1);
		}

		@Override
		public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
				EnumSkinModel rewriteModelId, GamePluginMessageProtocol protocol) {
			return new SPacketOtherSkinPresetEAG(rewriteUUIDMost, rewriteUUIDLeast, 1);
		}

		@Override
		public GameMessagePacket getForceSkinPacketV4() {
			return new SPacketForceClientSkinPresetV4EAG(1);
		}

		@Override
		public boolean isSkinPreset() {
			return true;
		}

		@Override
		public int getPresetSkinId() {
			return 1;
		}

		@Override
		public EnumPresetSkins getPresetSkin() {
			return EnumPresetSkins.DEFAULT_ALEX;
		}

		@Override
		public boolean isSkinCustom() {
			return false;
		}

		@Override
		public void getCustomSkinPixels_RGBA8_64x64(byte[] array, int offset) {
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

	};

	public static IEaglerPlayerSkin forPlayerUUID(UUID playerUUID) {
		return (playerUUID.hashCode() & 1) != 0 ? MISSING_SKIN_ALEX : MISSING_SKIN;
	}

}
