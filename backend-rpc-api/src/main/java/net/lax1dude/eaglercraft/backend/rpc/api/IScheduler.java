package net.lax1dude.eaglercraft.backend.rpc.api;

import javax.annotation.Nonnull;

public interface IScheduler {

	void execute(@Nonnull Runnable runnable);

	void executeAsync(@Nonnull Runnable runnable);

	void executeDelayed(@Nonnull Runnable runnable, long delay);

	void executeAsyncDelayed(@Nonnull Runnable runnable, long delay);

	@Nonnull
	ITask executeDelayedTask(@Nonnull Runnable runnable, long delay);

	@Nonnull
	ITask executeAsyncDelayedTask(@Nonnull Runnable runnable, long delay);

	@Nonnull
	ITask executeRepeatingTask(@Nonnull Runnable runnable, long delay, long interval);

	@Nonnull
	ITask executeAsyncRepeatingTask(@Nonnull Runnable runnable, long delay, long interval);

}
