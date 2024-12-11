package net.lax1dude.eaglerxserver.adapter;

public interface IPlatformConnection {

	<T> T getAttachment();

	boolean isEaglerConnection();

}
