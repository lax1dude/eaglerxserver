package net.lax1dude.eaglercraft.backend.server.api.skins;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IOptional;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public interface IEaglerPlayerCape extends IOptional<IEaglerPlayerCape> {

	@Nonnull
	GameMessagePacket getCapePacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			@Nonnull GamePluginMessageProtocol protocol);

	@Nonnull
	default GameMessagePacket getCapePacket(@Nonnull UUID rewriteUUID, @Nonnull GamePluginMessageProtocol protocol) {
		return getCapePacket(rewriteUUID.getMostSignificantBits(), rewriteUUID.getLeastSignificantBits(), protocol);
	}

	@Nonnull
	GameMessagePacket getForceCapePacketV4();

	boolean isCapeEnabled();

	boolean isCapePreset();

	int getPresetCapeId();

	@Nonnull
	EnumPresetCapes getPresetCape();

	boolean isCapeCustom();

	@Nonnull
	default byte[] getCustomCapePixels_ABGR8_32x32() {
		byte[] array = new byte[4096];
		getCustomCapePixels_ABGR8_32x32(array, 0);
		return array;
	}

	default void getCustomCapePixels_ABGR8_32x32(@Nonnull byte[] array) {
		getCustomCapePixels_ABGR8_32x32(array, 0);
	}

	void getCustomCapePixels_ABGR8_32x32(@Nonnull byte[] array, int offset);

	@Nonnull
	default byte[] getCustomCapePixels_eagler() {
		byte[] array = new byte[1173];
		getCustomCapePixels_eagler(array, 0);
		return array;
	}

	default void getCustomCapePixels_eagler(@Nonnull byte[] array) {
		getCustomCapePixels_eagler(array, 0);
	}

	void getCustomCapePixels_eagler(@Nonnull byte[] array, int offset);

}
