package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.ICustomPauseMenu;

class PauseMenuHelper {

	static ICustomPauseMenu wrap(net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu pauseMenu) {
		return new CustomPauseMenuLocal(pauseMenu);
	}

	static net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu unwrap(ICustomPauseMenu pauseMenu) {
		return ((CustomPauseMenuLocal) pauseMenu).pauseMenu;
	}

	static class CustomPauseMenuLocal implements ICustomPauseMenu {

		final net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu pauseMenu;

		CustomPauseMenuLocal(net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu pauseMenu) {
			this.pauseMenu = pauseMenu;
		}

	}

}
