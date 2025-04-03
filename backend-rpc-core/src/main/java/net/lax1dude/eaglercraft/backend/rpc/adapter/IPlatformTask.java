package net.lax1dude.eaglercraft.backend.rpc.adapter;

import net.lax1dude.eaglercraft.backend.rpc.api.ITask;

public interface IPlatformTask extends ITask {

	Runnable getTask();

}
