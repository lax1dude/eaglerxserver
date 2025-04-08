package net.lax1dude.eaglercraft.backend.rpc.base.local;

import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.EnumDiscordInviteButton;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.EnumServerInfoButton;
import net.lax1dude.eaglercraft.backend.rpc.api.pause_menu.ICustomPauseMenu;

class PauseMenuHelper {

	static ICustomPauseMenu wrap(net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu pauseMenu) {
		return new CustomPauseMenuLocal(pauseMenu);
	}

	static net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu unwrap(ICustomPauseMenu pauseMenu) {
		return ((CustomPauseMenuLocal) pauseMenu).pauseMenu;
	}

	static EnumServerInfoButton wrap(net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumServerInfoButton value) {
		return switch(value) {
		default -> EnumServerInfoButton.NONE;
		case EXTERNAL_URL -> EnumServerInfoButton.EXTERNAL_URL;
		case WEBVIEW_URL -> EnumServerInfoButton.WEBVIEW_URL;
		case WEBVIEW_BLOB -> EnumServerInfoButton.WEBVIEW_BLOB;
		};
	}

	static net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumServerInfoButton unwrap(EnumServerInfoButton value) {
		return switch(value) {
		default -> net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumServerInfoButton.NONE;
		case EXTERNAL_URL -> net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumServerInfoButton.EXTERNAL_URL;
		case WEBVIEW_URL -> net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumServerInfoButton.WEBVIEW_URL;
		case WEBVIEW_BLOB -> net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumServerInfoButton.WEBVIEW_BLOB;
		};
	}

	static EnumDiscordInviteButton wrap(net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumDiscordInviteButton value) {
		return switch(value) {
		default -> EnumDiscordInviteButton.NONE;
		case EXTERNAL_URL -> EnumDiscordInviteButton.EXTERNAL_URL;
		};
	}

	static net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumDiscordInviteButton unwrap(EnumDiscordInviteButton value) {
		return switch(value) {
		default -> net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumDiscordInviteButton.NONE;
		case EXTERNAL_URL -> net.lax1dude.eaglercraft.backend.server.api.pause_menu.EnumDiscordInviteButton.EXTERNAL_URL;
		};
	}

	static class CustomPauseMenuLocal implements ICustomPauseMenu {

		final net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu pauseMenu;

		CustomPauseMenuLocal(net.lax1dude.eaglercraft.backend.server.api.pause_menu.ICustomPauseMenu pauseMenu) {
			this.pauseMenu = pauseMenu;
		}

	}

}
