package net.lax1dude.eaglercraft.backend.server.bukkit;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformCommandSender;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.md_5.bungee.api.chat.BaseComponent;

class BukkitConsole implements IPlatformCommandSender<Player> {

	private final CommandSender console;

	BukkitConsole(CommandSender console) {
		this.console = console;
	}

	@Override
	public boolean checkPermission(String permission) {
		return console.hasPermission(permission);
	}

	@Override
	public <ComponentObject> void sendMessage(ComponentObject component) {
		console.sendMessage(((BaseComponent)component).toLegacyText());
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public IPlatformPlayer<Player> asPlayer() {
		return null;
	}

}
