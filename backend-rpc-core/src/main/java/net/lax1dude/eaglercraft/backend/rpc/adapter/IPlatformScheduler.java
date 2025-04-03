package net.lax1dude.eaglercraft.backend.rpc.adapter;

import net.lax1dude.eaglercraft.backend.rpc.api.IScheduler;

public interface IPlatformScheduler extends IScheduler {

	IPlatformTask executeDelayedTask(Runnable runnable, long delay);

	IPlatformTask executeAsyncDelayedTask(Runnable runnable, long delay);

	IPlatformTask executeRepeatingTask(Runnable runnable, long delay, long interval);

	IPlatformTask executeAsyncRepeatingTask(Runnable runnable, long delay, long interval);

}
