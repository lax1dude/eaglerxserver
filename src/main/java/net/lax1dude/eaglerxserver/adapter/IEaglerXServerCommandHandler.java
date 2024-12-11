package net.lax1dude.eaglerxserver.adapter;

public interface IEaglerXServerCommandHandler {

	void handle(IEaglerXServerCommandType command, IPlatformPlayer player, String[] args);

}
