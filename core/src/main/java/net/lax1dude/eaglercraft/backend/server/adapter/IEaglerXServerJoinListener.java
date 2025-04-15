package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerJoinListener<PlayerObject> {

	void handlePreConnect(IPlatformPlayer<PlayerObject> player);

	void handlePostConnect(IPlatformPlayer<PlayerObject> player, IPlatformServer<PlayerObject> server);

}
