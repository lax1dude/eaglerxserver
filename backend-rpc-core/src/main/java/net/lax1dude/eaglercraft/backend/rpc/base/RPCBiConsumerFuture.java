package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.function.BiConsumer;

import com.google.common.util.concurrent.AbstractFuture;

public abstract class RPCBiConsumerFuture<I1, I2, V> extends AbstractFuture<V> implements IRPCFutureAbstract<V>, BiConsumer<I1, I2> {

	private final SchedulerExecutors executors;

	protected RPCBiConsumerFuture(SchedulerExecutors executors) {
		this.executors = executors;
	}

	@Override
	public SchedulerExecutors getSchedulerExecutors() {
		return executors;
	}

}
