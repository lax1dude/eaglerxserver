package net.lax1dude.eaglercraft.backend.server.velocity;

import java.util.concurrent.TimeUnit;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;

class VelocityScheduler implements IPlatformScheduler {

	private final PlatformPluginVelocity plugin;
	private final Scheduler scheduler;

	private static class Task implements IPlatformTask {

		private final ScheduledTask task;
		private final Runnable runnable;

		protected Task(ScheduledTask task, Runnable runnable) {
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

	VelocityScheduler(PlatformPluginVelocity plugin, Scheduler scheduler) {
		this.plugin = plugin;
		this.scheduler = scheduler;
	}

	@Override
	public void executeAsync(Runnable runnable) {
		scheduler.buildTask(plugin, runnable).schedule();
	}

	@Override
	public void executeDelayed(Runnable runnable, long delay) {
		scheduler.buildTask(plugin, runnable).delay(delay, TimeUnit.MILLISECONDS).schedule();
	}

	@Override
	public void executeAsyncDelayed(Runnable runnable, long delay) {
		scheduler.buildTask(plugin, runnable).delay(delay, TimeUnit.MILLISECONDS).schedule();
	}

	@Override
	public IPlatformTask executeDelayedTask(Runnable runnable, long delay) {
		return new Task(scheduler.buildTask(plugin, runnable).delay(delay, TimeUnit.MILLISECONDS).schedule(), runnable);
	}

	@Override
	public IPlatformTask executeAsyncDelayedTask(Runnable runnable, long delay) {
		return new Task(scheduler.buildTask(plugin, runnable).delay(delay, TimeUnit.MILLISECONDS).schedule(), runnable);
	}

}
