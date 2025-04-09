package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.concurrent.Executor;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformScheduler;

public class SchedulerExecutors {

	public final Executor sync;
	public final Executor async;
	public final Executor tiny;

	public SchedulerExecutors(IPlatformScheduler scheduler, IPlatformLogger logger) {
		sync = scheduler::execute;
		async = scheduler::executeAsync;
		tiny = (runnable) -> {
			try {
				runnable.run();
			}catch(Exception ex) {
				logger.error("Caught exception from 'tiny' RPC future callback executor", ex);
			}
		};
	}

}
