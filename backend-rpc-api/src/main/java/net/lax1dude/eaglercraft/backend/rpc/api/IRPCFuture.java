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

	default void addCallback(FutureCallback<V> callback, Executor executor) {
		Futures.addCallback(this, callback, executor);
	}

	default void addCallback(FutureCallback<V> callback) {
		Futures.addCallback(this, callback, getScheduler());
	}

	default void addCallbackAsync(FutureCallback<V> callback) {
		Futures.addCallback(this, callback, getSchedulerAsync());
	}

	default void addCallbackTiny(FutureCallback<V> callback) {
		Futures.addCallback(this, callback, getSchedulerTiny());
	}

	boolean isTimedOut();

}
