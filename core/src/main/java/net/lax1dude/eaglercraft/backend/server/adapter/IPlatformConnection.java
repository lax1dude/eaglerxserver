package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformConnection {

	<T> T getAttachment();

	boolean isEaglerConnection();

}
