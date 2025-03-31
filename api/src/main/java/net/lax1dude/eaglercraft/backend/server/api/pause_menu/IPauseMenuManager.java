package net.lax1dude.eaglercraft.backend.server.api.pause_menu;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;

public interface IPauseMenuManager<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IPauseMenuService<PlayerObject> getPauseMenuService();

	default IWebViewManager<PlayerObject> getWebViewManager() {
		return getPlayer().getWebViewManager();
	}

	ICustomPauseMenu getActivePauseMenu();

	boolean isActivePauseMenuRemote();

	void updatePauseMenu(ICustomPauseMenu pauseMenu);

}
