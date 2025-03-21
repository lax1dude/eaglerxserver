package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketCustomizePauseMenuV4EAG;

class VanillaPauseMenu implements IPauseMenuImpl {

	static final IPauseMenuImpl INSTANCE = new VanillaPauseMenu();

	private final SPacketCustomizePauseMenuV4EAG packet = new SPacketCustomizePauseMenuV4EAG(0, null, null, null, 0,
			null, 0, null, null, null, null);

	private VanillaPauseMenu() {
	}

	@Override
	public SPacketCustomizePauseMenuV4EAG getPacket() {
		return packet;
	}

	@Override
	public IWebViewBlob getBlob() {
		return null;
	}

}
