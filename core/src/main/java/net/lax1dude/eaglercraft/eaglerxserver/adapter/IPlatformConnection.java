package net.lax1dude.eaglercraft.eaglerxserver.adapter;

public interface IPlatformConnection {

	<T> T getAttachment();

	boolean isEaglerConnection();

}
