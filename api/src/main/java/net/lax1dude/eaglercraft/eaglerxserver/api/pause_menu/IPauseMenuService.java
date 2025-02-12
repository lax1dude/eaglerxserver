package net.lax1dude.eaglercraft.eaglerxserver.api.pause_menu;

import net.lax1dude.eaglercraft.eaglerxserver.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.eaglerxserver.api.misc.IPacketImageLoader;
import net.lax1dude.eaglercraft.eaglerxserver.api.players.IEaglerPlayer;
import net.lax1dude.eaglercraft.eaglerxserver.api.webview.IWebViewService;

public interface IPauseMenuService<PlayerObject> extends IPacketImageLoader {

	IEaglerXServerAPI<PlayerObject> getServerAPI();

	default IPauseMenuManager<PlayerObject> getPauseMenuManager(PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getPauseMenuManager() : null;
	}

	default IPauseMenuManager<PlayerObject> getPauseMenuManager(IEaglerPlayer<PlayerObject> player) {
		return player.getPauseMenuManager();
	}

	IWebViewService<PlayerObject> getWebViewService();

	ICustomPauseMenu getVanillaPauseMenu();

	ICustomPauseMenu getDefaultPauseMenu();

	void setDefaultPauseMenu(ICustomPauseMenu pauseMenu);

	void updateAllPlayersPauseMenu(ICustomPauseMenu pauseMenu);

	IPauseMenuBuilder createPauseMenuBuilder();

}
