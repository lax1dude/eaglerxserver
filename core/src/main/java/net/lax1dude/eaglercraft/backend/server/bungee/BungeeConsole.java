package net.lax1dude.eaglercraft.backend.server.bungee;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformCommandSender;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeConsole implements IPlatformCommandSender<ProxiedPlayer> {

	private final CommandSender console;

	BungeeConsole(CommandSender console) {
		this.console = console;
	}

	@Override
	public boolean checkPermission(String permission) {
		return console.hasPermission(permission);
	}

	@Override
	public <ComponentObject> void sendMessage(ComponentObject component) {
		console.sendMessage((BaseComponent) component);
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public IPlatformPlayer<ProxiedPlayer> asPlayer() {
		return null;
	}

}
