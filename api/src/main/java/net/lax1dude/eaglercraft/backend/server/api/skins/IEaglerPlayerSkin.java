package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IOptional;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public interface IEaglerPlayerSkin extends IOptional<IEaglerPlayerSkin> {

	GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, GamePluginMessageProtocol protocol);

	default GameMessagePacket getSkinPacket(UUID rewriteUUID, GamePluginMessageProtocol protocol) {
		return getSkinPacket(rewriteUUID.getMostSignificantBits(), rewriteUUID.getLeastSignificantBits(), protocol);
	}

	GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, EnumSkinModel rewriteModelId, GamePluginMessageProtocol protocol);

	default GameMessagePacket getSkinPacket(UUID rewriteUUID, EnumSkinModel rewriteModelId, GamePluginMessageProtocol protocol) {
		return getSkinPacket(rewriteUUID.getMostSignificantBits(), rewriteUUID.getLeastSignificantBits(), rewriteModelId, protocol);
	}

	GameMessagePacket getForceSkinPacketV4();

	boolean isSkinPreset();

	int getPresetSkinId();

	EnumPresetSkins getPresetSkin();

	boolean isSkinCustom();

	default byte[] getCustomSkinPixels_RGBA8_64x64() {
		byte[] array = new byte[16384];
		getCustomSkinPixels_RGBA8_64x64(array, 0);
		return array;
	}

	default void getCustomSkinPixels_RGBA8_64x64(byte[] array) {
		getCustomSkinPixels_RGBA8_64x64(array, 0);
	}

	void getCustomSkinPixels_RGBA8_64x64(byte[] array, int offset);

	default byte[] getCustomSkinPixels_eagler() {
		byte[] array = new byte[12288];
		getCustomSkinPixels_eagler(array, 0);
		return array;
	}

	default void getCustomSkinPixels_eagler(byte[] array) {
		getCustomSkinPixels_eagler(array, 0);
	}

	void getCustomSkinPixels_eagler(byte[] array, int offset);

	EnumSkinModel getCustomSkinModelId();

}
