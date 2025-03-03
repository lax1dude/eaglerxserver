package net.lax1dude.eaglercraft.backend.server.base.skins;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetSkins;
import net.lax1dude.eaglercraft.backend.server.api.skins.EnumSkinModel;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
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
			throw new UnsupportedOperationException();
		}

		@Override
		public void getCustomSkinPixels_eagler(byte[] array, int offset) {
			throw new UnsupportedOperationException();
		}

		@Override
		public EnumSkinModel getCustomSkinModelId() {
			throw new UnsupportedOperationException();
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
			throw new UnsupportedOperationException();
		}

		@Override
		public void getCustomSkinPixels_eagler(byte[] array, int offset) {
			throw new UnsupportedOperationException();
		}

		@Override
		public EnumSkinModel getCustomSkinModelId() {
			throw new UnsupportedOperationException();
		}

	};

	public static final IEaglerPlayerCape MISSING_CAPE = new IEaglerPlayerCape() {

		private final GameMessagePacket packet = new SPacketOtherCapePresetEAG(0, 0, 0);

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
		public void getCustomCapePixels_RGBA8_32x32(byte[] array) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void getCustomCapePixels_eagler(byte[] array) {
			throw new UnsupportedOperationException();
		}

	};

}
