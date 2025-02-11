package net.lax1dude.eaglercraft.eaglerxserver.adapter;

public interface IEaglerXServerMessageChannel {

	String getLegacyName();

	String getModernName();

	IEaglerXServerMessageHandler getHandler();

}
