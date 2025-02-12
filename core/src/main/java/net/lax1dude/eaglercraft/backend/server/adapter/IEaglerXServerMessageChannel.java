package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IEaglerXServerMessageChannel {

	String getLegacyName();

	String getModernName();

	IEaglerXServerMessageHandler getHandler();

}
