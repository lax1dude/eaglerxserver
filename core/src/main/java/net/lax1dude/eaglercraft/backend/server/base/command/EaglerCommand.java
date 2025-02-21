package net.lax1dude.eaglercraft.backend.server.base.command;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandHandler;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerCommandType;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentHelper;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public abstract class EaglerCommand<PlayerObject>
		implements IEaglerXServerCommandType<PlayerObject>, IEaglerXServerCommandHandler<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;
	private final String name;
	private final String permission;
	private final String[] aliases;

	public EaglerCommand(EaglerXServer<PlayerObject> server, String name, String permission, String... aliases) {
		this.server = server;
		this.name = name;
		this.permission = permission;
		this.aliases = aliases;
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public String getPermission() {
		return permission;
	}

	@Override
	public String[] getCommandAliases() {
		return aliases;
	}

	public EaglerXServer<PlayerObject> getServer() {
		return server;
	}

	protected IPlatformComponentHelper getChatHelper() {
		return server.getPlatform().getComponentHelper();
	}

	protected IPlatformComponentBuilder getChatBuilder() {
		return server.getPlatform().getComponentHelper().builder();
	}

	@Override
	public IEaglerXServerCommandHandler<PlayerObject> getHandler() {
		return this;
	}

}
