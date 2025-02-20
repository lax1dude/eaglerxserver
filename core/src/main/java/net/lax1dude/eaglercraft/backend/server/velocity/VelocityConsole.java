package net.lax1dude.eaglercraft.backend.server.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformCommandSender;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;

class VelocityConsole implements IPlatformCommandSender<Player> {

	private final CommandSource console;

	VelocityConsole(CommandSource console) {
		this.console = console;
	}

	@Override
	public boolean checkPermission(String permission) {
		return console.hasPermission(permission);
	}

	@Override
	public <ComponentObject> void sendMessage(ComponentObject component) {
		console.sendMessage((Component) component);
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
