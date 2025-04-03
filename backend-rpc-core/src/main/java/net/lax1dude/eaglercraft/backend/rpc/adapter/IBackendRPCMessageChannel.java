package net.lax1dude.eaglercraft.backend.rpc.adapter;

public interface IBackendRPCMessageChannel<PlayerObject> {

	String getLegacyName();

	String getModernName();

	IBackendRPCMessageHandler<PlayerObject> getHandler();

}
