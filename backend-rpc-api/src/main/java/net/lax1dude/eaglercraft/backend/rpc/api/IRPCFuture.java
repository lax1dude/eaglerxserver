package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public interface IRPCFuture<V> extends ListenableFuture<V> {

	@Nonnull
	Executor getScheduler();

	@Nonnull
	Executor getSchedulerAsync();

	@Nonnull
	Executor getSchedulerTiny();

	/**
	 * Warning: Futures.addCallback is recommended!
	 */
	default void addListener(@Nonnull Runnable runnable) {
		addListener(runnable, getScheduler());
	}

	default void addListenerAsync(@Nonnull Runnable runnable) {
		addListener(runnable, getSchedulerAsync());
	}

	default void addListenerTiny(@Nonnull Runnable runnable) {
		addListener(runnable, getSchedulerTiny());
	}

	default void addCallback(@Nonnull FutureCallback<? super V> callback, @Nonnull Executor executor) {
		Futures.addCallback(this, callback, executor);
	}

	default void addCallback(@Nonnull FutureCallback<? super V> callback) {
		Futures.addCallback(this, callback, getScheduler());
	}

	default void addCallbackAsync(@Nonnull FutureCallback<? super V> callback) {
		Futures.addCallback(this, callback, getSchedulerAsync());
	}

	default void addCallbackTiny(@Nonnull FutureCallback<? super V> callback) {
		Futures.addCallback(this, callback, getSchedulerTiny());
	}

	boolean isTimedOut();

}
