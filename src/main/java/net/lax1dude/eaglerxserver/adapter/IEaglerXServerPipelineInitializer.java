package net.lax1dude.eaglerxserver.adapter;

public interface IEaglerXServerPipelineInitializer<A> {

	void initializeConnection(IEaglerXServerListener listener, IPlatformConnection conn,
			IPlatformConnectionInitializer<A> initializer);

}
