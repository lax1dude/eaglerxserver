package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerCommandHandler<PlayerObject> {

	void handle(IEaglerXServerCommandType command, IPlatformPlayer<PlayerObject> player, String[] args);

}
