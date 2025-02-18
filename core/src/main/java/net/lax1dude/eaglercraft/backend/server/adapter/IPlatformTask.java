package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformTask {

	Runnable getTask();

	void cancel();

}
