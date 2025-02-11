package net.lax1dude.eaglercraft.eaglerxserver.adapter;

public interface IEaglerXServerCommandType {

	String[] getCommandName();

	String getPermission();

	IEaglerXServerCommandHandler getHandler();

}
