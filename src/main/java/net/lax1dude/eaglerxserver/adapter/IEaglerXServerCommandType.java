package net.lax1dude.eaglerxserver.adapter;

public interface IEaglerXServerCommandType {

	String[] getCommandName();

	String getPermission();

	IEaglerXServerCommandHandler getHandler();

}
