package net.lax1dude.eaglercraft.backend.server.api.pause_menu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewManager;

public interface IPauseMenuManager<PlayerObject> {

	@Nonnull
	IEaglerPlayer<PlayerObject> getPlayer();

	@Nonnull
	IPauseMenuService<PlayerObject> getPauseMenuService();

	@Nullable
	default IWebViewManager<PlayerObject> getWebViewManager() {
		return getPlayer().getWebViewManager();
	}

	@Nonnull
	ICustomPauseMenu getActivePauseMenu();

	boolean isActivePauseMenuRemote();

	void updatePauseMenu(@Nonnull ICustomPauseMenu pauseMenu);

}
