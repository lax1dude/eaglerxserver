package net.lax1dude.eaglercraft.backend.server.base.skins;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientCapePresetV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketForceClientSkinPresetV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketOtherCapePresetEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.SkinPacketVersionCache;

public class MissingSkin {

	public static final IEaglerPlayerSkin MISSING_SKIN = new IEaglerPlayerSkin() {

		private final SkinPacketVersionCache packet = SkinPacketVersionCache.createPreset(0l, 0l, 0);

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public SkinPacketVersionCache getSkinPacketVersionCache(long rewriteUUIDMost, long rewriteUUIDLeast) {
			return SkinPacketVersionCache.rewriteUUID(packet, rewriteUUIDMost, rewriteUUIDLeast);
		}

		@Override
		public SkinPacketVersionCache getSkinPacketVersionCache(long rewriteUUIDMost, long rewriteUUIDLeast,
				EnumSkinModel rewriteModelId) {
			return SkinPacketVersionCache.rewriteUUIDModel(packet, rewriteUUIDMost, rewriteUUIDLeast,
					rewriteModelId.getId());
		}

		@Override
		public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
				GamePluginMessageProtocol protocol) {
			return packet.get(protocol, rewriteUUIDMost, rewriteUUIDLeast);
		}

		@Override
		public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
				EnumSkinModel rewriteModelId, GamePluginMessageProtocol protocol) {
			return packet.get(protocol, rewriteUUIDMost, rewriteUUIDLeast, rewriteModelId.getId());
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

		@Override
		public IEaglerPlayerSkin rewriteCustomSkinModelId(EnumSkinModel rewriteModelId) {
			throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
		}

	};

	public static final IEaglerPlayerSkin MISSING_SKIN_ALEX = new IEaglerPlayerSkin() {

		private final SkinPacketVersionCache packet = SkinPacketVersionCache.createPreset(0l, 0l, 1);

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public SkinPacketVersionCache getSkinPacketVersionCache(long rewriteUUIDMost, long rewriteUUIDLeast) {
			return SkinPacketVersionCache.rewriteUUID(packet, rewriteUUIDMost, rewriteUUIDLeast);
		}

		@Override
		public SkinPacketVersionCache getSkinPacketVersionCache(long rewriteUUIDMost, long rewriteUUIDLeast,
				EnumSkinModel rewriteModelId) {
			return SkinPacketVersionCache.rewriteUUIDModel(packet, rewriteUUIDMost, rewriteUUIDLeast,
					rewriteModelId.getId());
		}

		@Override
		public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
				GamePluginMessageProtocol protocol) {
			return packet.get(protocol, rewriteUUIDMost, rewriteUUIDLeast);
		}

		@Override
		public GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
				EnumSkinModel rewriteModelId, GamePluginMessageProtocol protocol) {
			return packet.get(protocol, rewriteUUIDMost, rewriteUUIDLeast, rewriteModelId.getId());
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

		@Override
		public IEaglerPlayerSkin rewriteCustomSkinModelId(EnumSkinModel rewriteModelId) {
			throw new UnsupportedOperationException("EaglerPlayerSkin is not a custom skin");
		}

	};

	public static final IEaglerPlayerCape MISSING_CAPE = new IEaglerPlayerCape() {

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public GameMessagePacket getCapePacket(long rewriteUUIDMost, long rewriteUUIDLeast,
				GamePluginMessageProtocol protocol) {
			return new SPacketOtherCapePresetEAG(rewriteUUIDMost, rewriteUUIDLeast, 0);
		}

		@Override
		public GameMessagePacket getForceCapePacketV4() {
			return new SPacketForceClientCapePresetV4EAG(0);
		}

		@Override
		public boolean isCapeEnabled() {
			return false;
		}

		@Override
		public boolean isCapePreset() {
			return true;
		}

		@Override
		public int getPresetCapeId() {
			return 0;
		}

		@Override
		public EnumPresetCapes getPresetCape() {
			return EnumPresetCapes.NO_CAPE;
		}

		@Override
		public boolean isCapeCustom() {
			return false;
		}

		@Override
		public void getCustomCapePixels_RGBA8_32x32(byte[] array, int offset) {
			throw new UnsupportedOperationException("EaglerPlayerCape is not a custom cape");
		}

		@Override
		public void getCustomCapePixels_eagler(byte[] array, int offset) {
			throw new UnsupportedOperationException("EaglerPlayerCape is not a custom cape");
		}

	};

	public static IEaglerPlayerSkin forPlayerUUID(UUID playerUUID) {
		return (playerUUID.hashCode() & 1) != 0 ? MISSING_SKIN_ALEX : MISSING_SKIN;
	}

}
