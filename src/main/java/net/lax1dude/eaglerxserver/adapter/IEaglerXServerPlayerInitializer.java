package net.lax1dude.eaglerxserver.adapter;

public interface IEaglerXServerPlayerInitializer<A> {

	void initializePlayer(IPlatformPlayer player, IPlatformPlayerInitializer<A> initializer);

}
