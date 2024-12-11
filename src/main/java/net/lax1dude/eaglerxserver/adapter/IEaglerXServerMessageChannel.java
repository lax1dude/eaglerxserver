package net.lax1dude.eaglerxserver.adapter;

public interface IEaglerXServerMessageChannel {

	String getLegacyName();

	String getModernName();

	IEaglerXServerMessageHandler getHandler();

}
