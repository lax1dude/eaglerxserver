package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerPlayerInitializer<A> {

	void initializePlayer(IPlatformPlayer player, IPlatformPlayerInitializer<A> initializer);

}
