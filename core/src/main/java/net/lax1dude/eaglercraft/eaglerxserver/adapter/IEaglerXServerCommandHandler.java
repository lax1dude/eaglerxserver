package net.lax1dude.eaglercraft.eaglerxserver.adapter;

public interface IEaglerXServerCommandHandler {

	void handle(IEaglerXServerCommandType command, IPlatformPlayer player, String[] args);

}
