package net.lax1dude.eaglercraft.backend.server.bukkit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;

class BukkitCommand extends Command {

	private final PlatformPluginBukkit plugin;
	private final IEaglerXServerCommandType<Player> cmd;

	protected BukkitCommand(PlatformPluginBukkit plugin, IEaglerXServerCommandType<Player> cmd) {
		super(cmd.getCommandName(), "EaglerXServer /" + cmd.getCommandName() + " command",
				"/" + cmd.getCommandName() + " [...]", nullFix(cmd.getCommandAliases()));
		String perm = cmd.getPermission();
		if(perm != null) {
			this.setPermission(perm);
		}
		this.plugin = plugin;
		this.cmd = cmd;
	}

	@Override
	public boolean execute(CommandSender var1, String var2, String[] var3) {
		cmd.getHandler().handle(cmd, plugin.getCommandSender(var1), var3);
		return true;
	}

	private static List<String> nullFix(String[] input) {
		return input != null ? Arrays.asList(input) : Collections.emptyList();
	}

}
