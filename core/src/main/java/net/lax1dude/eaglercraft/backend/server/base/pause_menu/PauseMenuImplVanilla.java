package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketCustomizePauseMenuV4EAG;

class PauseMenuImplVanilla implements IPauseMenuImpl {

	static final IPauseMenuImpl INSTANCE = new PauseMenuImplVanilla();
	static final IPauseMenuImpl INSTANCE_RPC = new PauseMenuImplVanilla();
	static final IPauseMenuImpl INSTANCE_RPC_2 = new PauseMenuImplVanilla() {
		@Override
		public boolean isPermitWebViewRequest() {
			return true;
		}
	};
	static final IPauseMenuImpl INSTANCE_RPC_3 = new PauseMenuImplVanilla() {
		@Override
		public boolean isPermitWebViewChannel() {
			return true;
		}
	};
	static final IPauseMenuImpl INSTANCE_RPC_4 = new PauseMenuImplVanilla() {
		@Override
		public boolean isPermitWebViewRequest() {
			return true;
		}
		@Override
		public boolean isPermitWebViewChannel() {
			return true;
		}
	};

	static IPauseMenuImpl getRPCMenu(SPacketCustomizePauseMenuV4EAG packet) {
		boolean request = packet.serverInfoMode == SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_SHOW_EMBED_OVER_WS;
		boolean channel = packet.serverInfoMode != SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_NONE
				&& packet.serverInfoMode != SPacketCustomizePauseMenuV4EAG.SERVER_INFO_MODE_EXTERNAL_URL
				&& (packet.serverInfoEmbedPerms & SPacketCustomizePauseMenuV4EAG.SERVER_INFO_EMBED_PERMS_MESSAGE_API) != 0;
		if(request) {
			return channel ? INSTANCE_RPC_4 : INSTANCE_RPC_2;
		}else {
			return channel ? INSTANCE_RPC_3 : INSTANCE_RPC;
		}
	}

	static final SPacketCustomizePauseMenuV4EAG PACKET = new SPacketCustomizePauseMenuV4EAG(0, null, null, null, 0,
			null, 0, null, null, null, null);

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
		return this != INSTANCE;
	}

}
