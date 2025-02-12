package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerCommandHandler {

	void handle(IEaglerXServerCommandType command, IPlatformPlayer player, String[] args);

}
