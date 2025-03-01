package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerMessageChannel<PlayerObject> {

	String getLegacyName();

	String getModernName();

	IEaglerXServerMessageHandler<PlayerObject> getHandler();

}
