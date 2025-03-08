package net.lax1dude.eaglercraft.backend.server.api;

public interface IScheduler {

	void execute(Runnable runnable);

	void executeAsync(Runnable runnable);

	void executeDelayed(Runnable runnable, long delay);

	void executeAsyncDelayed(Runnable runnable, long delay);

	ITask executeDelayedTask(Runnable runnable, long delay);

	ITask executeAsyncDelayedTask(Runnable runnable, long delay);

	ITask executeRepeatingTask(Runnable runnable, long delay, long interval);

	ITask executeAsyncRepeatingTask(Runnable runnable, long delay, long interval);

}
