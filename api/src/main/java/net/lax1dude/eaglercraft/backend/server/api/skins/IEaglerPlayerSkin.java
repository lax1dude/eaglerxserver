package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IOptional;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public interface IEaglerPlayerSkin extends IOptional<IEaglerPlayerSkin> {

	@Nonnull
	GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			@Nonnull GamePluginMessageProtocol protocol);

	@Nonnull
	default GameMessagePacket getSkinPacket(@Nonnull UUID rewriteUUID, @Nonnull GamePluginMessageProtocol protocol) {
		return getSkinPacket(rewriteUUID.getMostSignificantBits(), rewriteUUID.getLeastSignificantBits(), protocol);
	}

	@Nonnull
	GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, @Nonnull EnumSkinModel rewriteModelId,
			@Nonnull GamePluginMessageProtocol protocol);

	@Nonnull
	default GameMessagePacket getSkinPacket(@Nonnull UUID rewriteUUID, @Nonnull EnumSkinModel rewriteModelId,
			@Nonnull GamePluginMessageProtocol protocol) {
		return getSkinPacket(rewriteUUID.getMostSignificantBits(), rewriteUUID.getLeastSignificantBits(),
				rewriteModelId, protocol);
	}

	@Nonnull
	GameMessagePacket getForceSkinPacketV4();

	boolean isSkinPreset();

	int getPresetSkinId();

	@Nonnull
	EnumPresetSkins getPresetSkin();

	boolean isSkinCustom();

	@Nonnull
	default byte[] getCustomSkinPixels_ABGR8_64x64() {
		byte[] array = new byte[16384];
		getCustomSkinPixels_ABGR8_64x64(array, 0);
		return array;
	}

	default void getCustomSkinPixels_ABGR8_64x64(@Nonnull byte[] array) {
		getCustomSkinPixels_ABGR8_64x64(array, 0);
	}

	void getCustomSkinPixels_ABGR8_64x64(@Nonnull byte[] array, int offset);

	@Nonnull
	default byte[] getCustomSkinPixels_eagler() {
		byte[] array = new byte[12288];
		getCustomSkinPixels_eagler(array, 0);
		return array;
	}

	default void getCustomSkinPixels_eagler(@Nonnull byte[] array) {
		getCustomSkinPixels_eagler(array, 0);
	}

	void getCustomSkinPixels_eagler(@Nonnull byte[] array, int offset);

	@Nonnull
	EnumSkinModel getCustomSkinModelId();

}
