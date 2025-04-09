package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.concurrent.Executor;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public interface IRPCFuture<V> extends ListenableFuture<V> {

	Executor getScheduler();

	Executor getSchedulerAsync();

	Executor getSchedulerTiny();

	/**
	 * Warning: Futures.addCallback is recommended!
	 */
	default void addListener(Runnable runnable) {
		addListener(runnable, getScheduler());
	}

	default void addListenerAsync(Runnable runnable) {
		addListener(runnable, getSchedulerAsync());
	}

	default void addListenerTiny(Runnable runnable) {
		addListener(runnable, getSchedulerTiny());
	}

	default void addCallback(FutureCallback<V> runnable, Executor executor) {
		Futures.addCallback(this, runnable, executor);
	}

	default void addCallback(FutureCallback<V> runnable) {
		Futures.addCallback(this, runnable, getScheduler());
	}

	default void addCallbackAsync(FutureCallback<V> runnable) {
		Futures.addCallback(this, runnable, getSchedulerAsync());
	}

	default void addCallbackTiny(FutureCallback<V> runnable) {
		Futures.addCallback(this, runnable, getSchedulerTiny());
	}

	boolean isTimedOut();

}
