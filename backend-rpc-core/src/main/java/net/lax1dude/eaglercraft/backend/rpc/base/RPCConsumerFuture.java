package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.function.Consumer;

import com.google.common.util.concurrent.AbstractFuture;

public abstract class RPCConsumerFuture<I, V> extends AbstractFuture<V> implements IRPCFutureAbstract<V>, Consumer<I> {

	private final SchedulerExecutors executors;

	protected RPCConsumerFuture(SchedulerExecutors executors) {
		this.executors = executors;
	}

	@Override
	public SchedulerExecutors getSchedulerExecutors() {
		return executors;
	}

}
