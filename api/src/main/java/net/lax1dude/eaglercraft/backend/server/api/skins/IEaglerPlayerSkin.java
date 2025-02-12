package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.UUID;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.SkinPacketVersionCache;

public interface IEaglerPlayerSkin {

	SkinPacketVersionCache getSkinPacketVersionCache();

	GameMessagePacket getSkinPacket(GamePluginMessageProtocol protocol);

	GameMessagePacket getSkinPacket(long rewriteUUIDMost, long rewriteUUIDLeast, GamePluginMessageProtocol protocol);

	default GameMessagePacket getSkinPacket(UUID rewriteUUID, GamePluginMessageProtocol protocol) {
		return getSkinPacket(rewriteUUID.getMostSignificantBits(), rewriteUUID.getLeastSignificantBits(), protocol);
	}

	boolean isSkinPreset();

	int getPresetSkinId();

	EnumPresetSkins getPresetSkin();

	boolean isSkinCustom();

	void getCustomSkinPixels_RGBA8_64x64(byte[] array);

	void getCustomSkinPixels_eagler(byte[] array);

	int getCustomSkinModelId();

}
