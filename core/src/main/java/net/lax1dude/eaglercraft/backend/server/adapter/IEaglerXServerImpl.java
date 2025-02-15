package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerImpl<PlayerObject> {

	void load(IPlatform.Init<PlayerObject> platf);

}
