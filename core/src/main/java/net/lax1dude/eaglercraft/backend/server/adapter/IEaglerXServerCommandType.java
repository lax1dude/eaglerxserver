package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerCommandType<PlayerObject> {

	String getCommandName();

	String getPermission();

	String[] getCommandAliases();

	IEaglerXServerCommandHandler<PlayerObject> getHandler();

}
