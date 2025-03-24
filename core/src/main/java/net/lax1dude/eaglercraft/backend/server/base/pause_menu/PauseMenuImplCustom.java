package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketCustomizePauseMenuV4EAG;

class PauseMenuImplCustom implements IPauseMenuImpl {

	private final SPacketCustomizePauseMenuV4EAG packet;
	private final IWebViewBlob blob;
	private final boolean permitChannel;

	PauseMenuImplCustom(SPacketCustomizePauseMenuV4EAG packet, IWebViewBlob blob, boolean permitChannel) {
		this.packet = packet;
		this.blob = blob;
		this.permitChannel = permitChannel;
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

}
