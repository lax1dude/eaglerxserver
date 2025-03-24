package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketCustomizePauseMenuV4EAG;

public interface IPauseMenuImpl extends ICustomPauseMenu {

	SPacketCustomizePauseMenuV4EAG getPacket();

	IWebViewBlob getBlob();

	boolean isPermitWebViewChannel();

}
