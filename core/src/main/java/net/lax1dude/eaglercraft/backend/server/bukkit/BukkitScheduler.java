package net.lax1dude.eaglercraft.backend.server.bukkit;

import org.bukkit.scheduler.BukkitTask;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;

class BukkitScheduler implements IPlatformScheduler {

	private final PlatformPluginBukkit plugin;
	private final org.bukkit.scheduler.BukkitScheduler scheduler;

	private static class Task implements IPlatformTask {

		private final BukkitTask task;
		private final Runnable runnable;

		protected Task(BukkitTask task, Runnable runnable) {
			this.task = task;
			this.runnable = runnable;
		}

		@Override
		public Runnable getTask() {
			return runnable;
		}

		@Override
		public void cancel() {
			task.cancel();
		}

	}

	public BukkitScheduler(PlatformPluginBukkit plugin, org.bukkit.scheduler.BukkitScheduler scheduler) {
		this.plugin = plugin;
		this.scheduler = scheduler;
	}

	@Override
	public void executeAsync(Runnable runnable) {
		scheduler.runTaskAsynchronously(plugin, runnable);
	}

	@Override
	public void executeDelayed(Runnable runnable, long delay) {
		scheduler.runTaskLater(plugin, runnable, delay);
	}

	@Override
	public void executeAsyncDelayed(Runnable runnable, long delay) {
		scheduler.runTaskLaterAsynchronously(plugin, runnable, delay);
	}

	@Override
	public IPlatformTask executeDelayedTask(Runnable runnable, long delay) {
		return new Task(scheduler.runTaskLater(plugin, runnable, delay), runnable);
	}

	@Override
	public IPlatformTask executeAsyncDelayedTask(Runnable runnable, long delay) {
		return new Task(scheduler.runTaskLaterAsynchronously(plugin, runnable, delay), runnable);
	}

}
