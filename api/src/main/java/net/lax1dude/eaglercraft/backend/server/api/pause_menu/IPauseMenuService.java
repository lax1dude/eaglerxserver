package net.lax1dude.eaglercraft.backend.server.api.pause_menu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.IPacketImageLoader;
import net.lax1dude.eaglercraft.backend.server.api.webview.IWebViewService;

public interface IPauseMenuService<PlayerObject> extends IPacketImageLoader {

	@Nonnull
	IEaglerXServerAPI<PlayerObject> getServerAPI();

	@Nullable
	default IPauseMenuManager<PlayerObject> getPauseMenuManager(@Nonnull PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getPauseMenuManager() : null;
	}

	@Nonnull
	IWebViewService<PlayerObject> getWebViewService();

	@Nonnull
	ICustomPauseMenu getVanillaPauseMenu();

	@Nonnull
	ICustomPauseMenu getDefaultPauseMenu();

	void setDefaultPauseMenu(@Nonnull ICustomPauseMenu pauseMenu);

	void updateAllPlayersPauseMenu(@Nonnull ICustomPauseMenu pauseMenu);

	@Nonnull
	IPauseMenuBuilder createPauseMenuBuilder();

}
