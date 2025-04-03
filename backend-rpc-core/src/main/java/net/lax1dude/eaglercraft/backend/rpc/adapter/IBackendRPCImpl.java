package net.lax1dude.eaglercraft.backend.rpc.adapter;

public interface IBackendRPCImpl<PlayerObject> {

	void load(IPlatform.Init<PlayerObject> platf);

}
