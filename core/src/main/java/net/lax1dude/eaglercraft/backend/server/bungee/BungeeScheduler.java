package net.lax1dude.eaglercraft.backend.server.bungee;

import java.util.concurrent.TimeUnit;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;

class BungeeScheduler implements IPlatformScheduler {

	private final PlatformPluginBungee plugin;
	private final TaskScheduler scheduler;

	private class Task implements IPlatformTask {

		private final ScheduledTask task;

		protected Task(ScheduledTask task) {
			this.task = task;
		}

		@Override
		public Runnable getTask() {
			return task.getTask();
		}

		@Override
		public void cancel() {
			task.cancel();
		}

	}

	BungeeScheduler(PlatformPluginBungee plugin, TaskScheduler scheduler) {
		this.plugin = plugin;
		this.scheduler = scheduler;
	}

	@Override
	public void executeAsync(Runnable runnable) {
		scheduler.runAsync(plugin, runnable);
	}

	@Override
	public void executeDelayed(Runnable runnable, long delay) {
		scheduler.schedule(plugin, runnable, delay, TimeUnit.MILLISECONDS);
	}

	@Override
	public void executeAsyncDelayed(Runnable runnable, long delay) {
		scheduler.schedule(plugin, runnable, delay, TimeUnit.MILLISECONDS);
	}

	@Override
	public IPlatformTask executeDelayedTask(Runnable runnable, long delay) {
		return new Task(scheduler.schedule(plugin, runnable, delay, TimeUnit.MILLISECONDS));
	}

	@Override
	public IPlatformTask executeAsyncDelayedTask(Runnable runnable, long delay) {
		return new Task(scheduler.schedule(plugin, runnable, delay, TimeUnit.MILLISECONDS));
	}

}
