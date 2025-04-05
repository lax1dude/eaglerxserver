package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RPCImmediateFuture<V> implements IRPCFutureAbstract<V> {

	public static <V> RPCImmediateFuture<V> create(SchedulerExecutors executors, V value) {
		return new RPCImmediateFuture<>(executors, value);
	}

	private final SchedulerExecutors executors;
	private final V value;

	private RPCImmediateFuture(SchedulerExecutors executors, V value) {
		this.executors = executors;
		this.value = value;
	}

	@Override
	public SchedulerExecutors getSchedulerExecutors() {
		return executors;
	}

	@Override
	public void addListener(Runnable arg0, Executor arg1) {
		arg1.execute(arg0);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		return value;
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return value;
	}

}
