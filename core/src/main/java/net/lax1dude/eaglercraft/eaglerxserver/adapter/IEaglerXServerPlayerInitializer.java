package net.lax1dude.eaglercraft.eaglerxserver.adapter;

public interface IEaglerXServerPlayerInitializer<A> {

	void initializePlayer(IPlatformPlayer player, IPlatformPlayerInitializer<A> initializer);

}
