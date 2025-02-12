package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerPipelineInitializer<A> {

	void initializeConnection(IEaglerXServerListener listener, IPlatformConnection conn,
			IPlatformConnectionInitializer<A> initializer);

}
