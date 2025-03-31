package net.lax1dude.eaglercraft.backend.server.base.pause_menu;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuManager;
import net.lax1dude.eaglercraft.backend.server.api.pause_menu.IPauseMenuService;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewBlob;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketCustomizePauseMenuV4EAG;

public class PauseMenuManager<PlayerObject> implements IPauseMenuManager<PlayerObject> {

	private final EaglerPlayerInstance<PlayerObject> player;
	private final PauseMenuService<PlayerObject> service;
	private IPauseMenuImpl activePauseMenu = PauseMenuImplVanilla.INSTANCE;

	PauseMenuManager(EaglerPlayerInstance<PlayerObject> player, PauseMenuService<PlayerObject> service) {
		this.player = player;
		this.service = service;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public IPauseMenuService<PlayerObject> getPauseMenuService() {
		return service;
	}

	@Override
	public ICustomPauseMenu getActivePauseMenu() {
		return activePauseMenu.extern();
	}

	@Override
	public boolean isActivePauseMenuRemote() {
		return activePauseMenu.isRemote();
	}

	@Override
	public void updatePauseMenu(ICustomPauseMenu pauseMenu) {
		IPauseMenuImpl impl = (IPauseMenuImpl) pauseMenu;
		if(activePauseMenu == impl) return;
		activePauseMenu = impl;
		player.sendEaglerMessage(impl.getPacket());
	}

	public boolean isWebViewChannelAllowedDefault() {
		return activePauseMenu.isPermitWebViewChannel();
	}

	public boolean isWebViewRequestAllowedDefault() {
		return activePauseMenu.isPermitWebViewRequest();
	}

	public IWebViewBlob getWebViewBlobDefault() {
		return activePauseMenu.getBlob();
	}

	public void updatePauseMenuRPC(SPacketCustomizePauseMenuV4EAG packet) {
		activePauseMenu = PauseMenuImplVanilla.INSTANCE_RPC;
		player.sendEaglerMessage(packet);
	}

}
