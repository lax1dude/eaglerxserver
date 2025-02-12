package net.lax1dude.eaglercraft.eaglerxserver.api.skins;

import java.util.UUID;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public interface IEaglerPlayerCape {

	GameMessagePacket getCapePacket(GamePluginMessageProtocol protocol);

	GameMessagePacket getCapePacket(long rewriteUUIDMost, long rewriteUUIDLeast, GamePluginMessageProtocol protocol);

	default GameMessagePacket getCapePacket(UUID rewriteUUID, GamePluginMessageProtocol protocol) {
		return getCapePacket(rewriteUUID.getMostSignificantBits(), rewriteUUID.getLeastSignificantBits(), protocol);
	}

	boolean isCapeEnabled();

	boolean isCapePreset();

	int getPresetCapeId();

	EnumPresetCapes getPresetCape();

	boolean isCapeCustom();

	void getCustomCapePixels_RGBA8_32x32(byte[] array);

	void getCustomCapePixels_eagler(byte[] array);

	int getCustomSkinModelId();

}
