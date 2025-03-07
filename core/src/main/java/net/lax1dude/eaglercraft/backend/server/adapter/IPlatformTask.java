package net.lax1dude.eaglercraft.backend.server.adapter;

import net.lax1dude.eaglercraft.backend.server.api.ITask;

public interface IPlatformTask extends ITask {

	Runnable getTask();

}
