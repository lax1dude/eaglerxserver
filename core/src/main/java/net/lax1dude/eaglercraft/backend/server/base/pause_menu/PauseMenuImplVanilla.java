package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketCustomizePauseMenuV4EAG;

class PauseMenuImplVanilla implements IPauseMenuImpl {

	static final IPauseMenuImpl INSTANCE = new PauseMenuImplVanilla();
	static final IPauseMenuImpl INSTANCE_RPC = new PauseMenuImplVanilla();

	static final SPacketCustomizePauseMenuV4EAG PACKET = new SPacketCustomizePauseMenuV4EAG(0, null, null, null, 0,
			null, 0, null, null, null, null);

	private PauseMenuImplVanilla() {
	}

	@Override
	public SPacketCustomizePauseMenuV4EAG getPacket() {
		return PACKET;
	}

	@Override
	public IWebViewBlob getBlob() {
		return null;
	}

	@Override
	public boolean isPermitWebViewChannel() {
		return false;
	}

	@Override
	public boolean isPermitWebViewRequest() {
		return false;
	}

	@Override
	public ICustomPauseMenu extern() {
		return INSTANCE;
	}

	@Override
	public boolean isRemote() {
		return this == INSTANCE_RPC;
	}

}
