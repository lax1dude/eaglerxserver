package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.lax1dude.eaglercraft.backend.rpc.api.RPCTimeoutException;

public class RPCFailedFuture<V> implements IRPCFutureAbstract<V> {

	public static <V> RPCFailedFuture<V> create(SchedulerExecutors executors, Throwable error) {
		return new RPCFailedFuture<>(executors, error);
	}

	public static <V> RPCFailedFuture<V> createClosed(SchedulerExecutors executors) {
		return create(executors, new RPCTimeoutException("RPC context is closed"));
	}

	private final SchedulerExecutors executors;
	private final Throwable error;

	private RPCFailedFuture(SchedulerExecutors executors, Throwable error) {
		this.executors = executors;
		this.error = error;
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
		throw new ExecutionException(error);
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		throw new ExecutionException(error);
	}

	@Override
	public boolean isTimedOut() {
		return error instanceof RPCTimeoutException;
	}

}
