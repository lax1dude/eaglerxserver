package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.concurrent.Executor;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformScheduler;

public class SchedulerExecutors {

	public final Executor sync;
	public final Executor async;

	public SchedulerExecutors(IPlatformScheduler scheduler) {
		sync = scheduler::execute;
		async = scheduler::executeAsync;
	}

}
