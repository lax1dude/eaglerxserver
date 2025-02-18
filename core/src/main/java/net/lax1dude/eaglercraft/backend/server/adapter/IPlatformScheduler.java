package net.lax1dude.eaglercraft.backend.server.adapter;

public interface IPlatformScheduler {

	void executeAsync(Runnable runnable);

	void executeDelayed(Runnable runnable, long delay);

	void executeAsyncDelayed(Runnable runnable, long delay);

	IPlatformTask executeDelayedTask(Runnable runnable, long delay);

	IPlatformTask executeAsyncDelayedTask(Runnable runnable, long delay);

}
