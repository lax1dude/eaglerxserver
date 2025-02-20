package net.lax1dude.eaglercraft.backend.server.velocity;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;

class VelocityCommand implements SimpleCommand {

	private final PlatformPluginVelocity plugin;
	private final IEaglerXServerCommandType<Player> cmd;

	VelocityCommand(PlatformPluginVelocity plugin, IEaglerXServerCommandType<Player> cmd) {
		this.plugin = plugin;
		this.cmd = cmd;
	}

	@Override
	public void execute(Invocation arg0) {
		cmd.getHandler().handle(cmd, plugin.getCommandSender(arg0.source()), arg0.arguments());
	}

	@Override
    public boolean hasPermission(Invocation invocation) {
		String perm = cmd.getPermission();
		return perm == null || invocation.source().hasPermission(perm);
	}

	public CommandMeta register() {
		CommandManager cmdManager = plugin.proxy().getCommandManager();
		CommandMeta.Builder builder = cmdManager.metaBuilder(cmd.getCommandName());
		builder.plugin(plugin);
		String[] aliases = cmd.getCommandAliases();
		if(aliases != null && aliases.length > 0) {
			builder.aliases(aliases);
		}
		CommandMeta ret = builder.build();
		cmdManager.register(ret, this);
		return ret;
	}

}
