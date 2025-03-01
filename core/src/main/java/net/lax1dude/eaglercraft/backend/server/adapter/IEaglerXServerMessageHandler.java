package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerMessageHandler<PlayerObject> {

	void handle(IEaglerXServerMessageChannel<PlayerObject> channel, IPlatformPlayer<PlayerObject> player, byte[] contents);

}
