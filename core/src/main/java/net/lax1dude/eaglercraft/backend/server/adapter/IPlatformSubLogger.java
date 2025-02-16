package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformSubLogger extends IPlatformLogger {

	IPlatformLogger getParent();

	String getName();

	void setName(String name);

}
