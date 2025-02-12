package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerMessageHandler {

	void handle(IEaglerXServerMessageChannel channel, IPlatformPlayer player, byte[] contents);

}
