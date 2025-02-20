package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerCommandHandler<PlayerObject> {

	void handle(IEaglerXServerCommandType<PlayerObject> command, IPlatformCommandSender<PlayerObject> player, String[] args);

}
