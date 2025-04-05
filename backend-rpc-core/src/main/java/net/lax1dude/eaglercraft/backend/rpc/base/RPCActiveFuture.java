package net.lax1dude.eaglercraft.backend.rpc.base;

import com.google.common.util.concurrent.AbstractFuture;

public class RPCActiveFuture<V> extends AbstractFuture<V> implements IRPCFutureExpiring<V> {

	private final SchedulerExecutors executors;
	private final long expiresAt;

	RPCActiveFuture(SchedulerExecutors executors, long expiresAt) {
		this.executors = executors;
		this.expiresAt = expiresAt;
	}

	@Override
	public SchedulerExecutors getSchedulerExecutors() {
		return executors;
	}

	@Override
	public long expiresAt() {
		return expiresAt;
	}

	public void fireCompleteInternal(V value) {
		this.set(value);
	}

	public void fireExceptionInternal(Throwable value) {
		this.setException(value);
	}

}
