package net.lax1dude.eaglercraft.eaglerxserver.api.pause_menu;

import net.lax1dude.eaglercraft.eaglerxserver.api.players.IEaglerPlayer;
import net.lax1dude.eaglercraft.eaglerxserver.api.webview.IWebViewManager;

public interface IPauseMenuManager<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IPauseMenuService<PlayerObject> getPauseMenuService();

	IWebViewManager<PlayerObject> getWebViewManager();

	ICustomPauseMenu getActivePauseMenu();

	void updatePauseMenu(ICustomPauseMenu pauseMenu);

}
