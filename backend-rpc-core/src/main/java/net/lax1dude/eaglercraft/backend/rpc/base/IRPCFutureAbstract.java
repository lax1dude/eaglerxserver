package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.concurrent.Executor;

import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;

public interface IRPCFutureAbstract<V> extends IRPCFuture<V> {

	SchedulerExecutors getSchedulerExecutors();

	default Executor getScheduler() {
		return getSchedulerExecutors().sync;
	}

	default Executor getSchedulerAsync() {
		return getSchedulerExecutors().async;
	}

}
