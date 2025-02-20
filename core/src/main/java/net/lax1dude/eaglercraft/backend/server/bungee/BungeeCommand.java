package net.lax1dude.eaglercraft.backend.server.bungee;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

class BungeeCommand extends Command {

	private final PlatformPluginBungee plugin;
	private final IEaglerXServerCommandType<ProxiedPlayer> cmd;

	public BungeeCommand(PlatformPluginBungee plugin, IEaglerXServerCommandType<ProxiedPlayer> cmd) {
		super(cmd.getCommandName(), cmd.getPermission(), nullFix(cmd.getCommandAliases()));
		this.plugin = plugin;
		this.cmd = cmd;
	}

	@Override
	public void execute(CommandSender arg0, String[] arg1) {
		cmd.getHandler().handle(cmd, plugin.getCommandSender(arg0), arg1);
	}

	private static String[] nullFix(String[] input) {
		return input != null ? input : new String[0];
	}

}
