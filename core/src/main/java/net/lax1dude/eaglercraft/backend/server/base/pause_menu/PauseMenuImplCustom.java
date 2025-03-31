package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketCustomizePauseMenuV4EAG;

class PauseMenuImplCustom implements IPauseMenuImpl {

	private final SPacketCustomizePauseMenuV4EAG packet;
	private final IWebViewBlob blob;
	private final boolean permitChannel;
	private final boolean permitRequest;

	PauseMenuImplCustom(SPacketCustomizePauseMenuV4EAG packet, IWebViewBlob blob, boolean permitChannel) {
		this.packet = packet;
		this.blob = blob;
		this.permitChannel = permitChannel;
		this.permitRequest = packet.serverInfoMode == SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_SHOW_EMBED_OVER_WS;
	}

	@Override
	public SPacketCustomizePauseMenuV4EAG getPacket() {
		return packet;
	}

	@Override
	public IWebViewBlob getBlob() {
		return blob;
	}

	@Override
	public boolean isPermitWebViewChannel() {
		return permitChannel;
	}

	@Override
	public boolean isPermitWebViewRequest() {
		return permitRequest;
	}

	@Override
	public ICustomPauseMenu extern() {
		return this;
	}

	@Override
	public boolean isRemote() {
		return false;
	}

}
