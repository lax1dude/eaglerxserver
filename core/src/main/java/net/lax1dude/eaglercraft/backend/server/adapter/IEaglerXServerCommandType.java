package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerCommandType {

	String[] getCommandName();

	String getPermission();

	IEaglerXServerCommandHandler getHandler();

}
