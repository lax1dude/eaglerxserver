package net.lax1dude.eaglercraft.backend.server.api.pause_menu;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;

public interface IPauseMenuManager<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IPauseMenuService<PlayerObject> getPauseMenuService();

	IWebViewManager<PlayerObject> getWebViewManager();

	ICustomPauseMenu getActivePauseMenu();

	void updatePauseMenu(ICustomPauseMenu pauseMenu);

}
