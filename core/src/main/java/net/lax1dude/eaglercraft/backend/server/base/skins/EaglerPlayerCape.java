package net.lax1dude.eaglercraft.backend.server.base.skins;

import net.lax1dude.eaglercraft.backend.server.api.skins.EnumPresetCapes;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public class EaglerPlayerCape implements IEaglerPlayerCape {

	public EaglerPlayerCape(GameMessagePacket cape) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isSuccess() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GameMessagePacket getCapePacket(long rewriteUUIDMost, long rewriteUUIDLeast,
			GamePluginMessageProtocol protocol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCapeEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCapePreset() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getPresetCapeId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public EnumPresetCapes getPresetCape() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCapeCustom() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void getCustomCapePixels_RGBA8_32x32(byte[] array) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getCustomCapePixels_eagler(byte[] array) {
		// TODO Auto-generated method stub

	}

}
