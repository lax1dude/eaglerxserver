package net.lax1dude.eaglercraft.backend.rpc.base;

import com.google.common.util.concurrent.AbstractFuture;

public class RPCActiveFuture<V> extends AbstractFuture<V> implements IRPCFutureExpiring<V> {

	public static <V> RPCActiveFuture<V> create(SchedulerExecutors executors, long now, int expiresAfter) {
		return new RPCActiveFuture<>(executors, now + expiresAfter * 1000000000l);
	}

	private final SchedulerExecutors executors;
	private final long expiresAt;
	private boolean timedOut;

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

	public boolean fireCompleteInternal(V value) {
		return set(value);
	}

	public boolean fireExceptionInternal(Throwable value) {
		return setException(value);
	}

	@Override
	public boolean fireTimeoutExceptionInternal(Throwable value) {
		if(fireExceptionInternal(value)) {
			timedOut = true;
			return true;
		}else {
			return false;
		}
	}

	public boolean isTimedOut() {
		return timedOut;
	}

}
