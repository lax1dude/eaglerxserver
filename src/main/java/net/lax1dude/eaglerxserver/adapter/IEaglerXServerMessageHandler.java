package net.lax1dude.eaglerxserver.adapter;

public interface IEaglerXServerMessageHandler {

	void handle(IEaglerXServerMessageChannel channel, IPlatformPlayer player, byte[] contents);

}
