package net.lax1dude.eaglercraft.backend.rpc.adapter;

public interface IBackendRPCMessageHandler<PlayerObject> {
	void handle(IBackendRPCMessageChannel<PlayerObject> channel, IPlatformPlayer<PlayerObject> player, byte[] contents);
}
