package net.lax1dude.eaglercraft.backend.server.adapter;

import net.lax1dude.eaglercraft.backend.server.api.IScheduler;

public interface IPlatformScheduler extends IScheduler {

	IPlatformTask executeDelayedTask(Runnable runnable, long delay);

	IPlatformTask executeAsyncDelayedTask(Runnable runnable, long delay);

	IPlatformTask executeRepeatingTask(Runnable runnable, long delay, long interval);

	IPlatformTask executeAsyncRepeatingTask(Runnable runnable, long delay, long interval);

}
