package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IOptional;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public interface IEaglerPlayerCape extends IOptional<IEaglerPlayerCape> {

	GameMessagePacket getCapePacket(long rewriteUUIDMost, long rewriteUUIDLeast, GamePluginMessageProtocol protocol);

	default GameMessagePacket getCapePacket(UUID rewriteUUID, GamePluginMessageProtocol protocol) {
		return getCapePacket(rewriteUUID.getMostSignificantBits(), rewriteUUID.getLeastSignificantBits(), protocol);
	}

	GameMessagePacket getForceCapePacketV4();

	boolean isCapeEnabled();

	boolean isCapePreset();

	int getPresetCapeId();

	EnumPresetCapes getPresetCape();

	boolean isCapeCustom();

	default byte[] getCustomCapePixels_RGBA8_32x32() {
		byte[] array = new byte[4096];
		getCustomCapePixels_RGBA8_32x32(array, 0);
		return array;
	}

	default void getCustomCapePixels_RGBA8_32x32(byte[] array) {
		getCustomCapePixels_RGBA8_32x32(array, 0);
	}

	void getCustomCapePixels_RGBA8_32x32(byte[] array, int offset);

	default byte[] getCustomCapePixels_eagler() {
		byte[] array = new byte[1173];
		getCustomCapePixels_eagler(array, 0);
		return array;
	}

	default void getCustomCapePixels_eagler(byte[] array) {
		getCustomCapePixels_eagler(array, 0);
	}

	void getCustomCapePixels_eagler(byte[] array, int offset);

}
