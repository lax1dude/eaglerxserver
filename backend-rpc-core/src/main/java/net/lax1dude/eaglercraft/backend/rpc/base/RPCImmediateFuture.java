package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;

public class RPCImmediateFuture<V> implements IRPCFuture<V> {

	public static <V> RPCImmediateFuture<V> create(V value) {
		return new RPCImmediateFuture<>(value);
	}

	private final V value;

	private RPCImmediateFuture(V value) {
		this.value = value;
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

	@Override
	public void setExpiresMSFromNow(int millis) {
	}

	@Override
	public boolean hasExpired() {
		return false;
	}

}
