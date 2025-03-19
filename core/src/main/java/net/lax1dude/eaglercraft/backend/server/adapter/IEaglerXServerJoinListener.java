package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerJoinListener<PlayerObject> {

	void handle(IPlatformPlayer<PlayerObject> player, IPlatformServer<PlayerObject> server);

}
