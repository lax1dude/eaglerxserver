package net.lax1dude.eaglercraft.backend.server.api.pause_menu;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;

public interface IPauseMenuService<PlayerObject> extends IPacketImageLoader {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	default IPauseMenuManager<PlayerObject> getPauseMenuManager(PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getPauseMenuManager() : null;
	}

	IWebViewService<PlayerObject> getWebViewService();

	ICustomPauseMenu getVanillaPauseMenu();

	ICustomPauseMenu getDefaultPauseMenu();

	void setDefaultPauseMenu(ICustomPauseMenu pauseMenu);

	void updateAllPlayersPauseMenu(ICustomPauseMenu pauseMenu);

	IPauseMenuBuilder createPauseMenuBuilder();

}
