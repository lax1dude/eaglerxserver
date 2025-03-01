package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;
import net.lax1dude.eaglercraft.backend.server.api.IScheduler;
import net.lax1dude.eaglercraft.backend.server.api.ITask;

public class Scheduler implements IScheduler {

	private static class Task implements ITask {

		private final IPlatformTask task;

		protected Task(IPlatformTask task) {
			this.task = task;
		}

		@Override
		public void cancel() {
			task.cancel();
		}

	}

	private final IPlatformScheduler scheduler;

	public Scheduler(IPlatformScheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public void executeAsync(Runnable runnable) {
		scheduler.executeAsync(runnable);
	}

	@Override
	public void executeDelayed(Runnable runnable, long delay) {
		scheduler.executeDelayed(runnable, delay);
	}

	@Override
	public void executeAsyncDelayed(Runnable runnable, long delay) {
		scheduler.executeAsyncDelayed(runnable, delay);
	}

	@Override
	public ITask executeDelayedTask(Runnable runnable, long delay) {
		return new Task(scheduler.executeDelayedTask(runnable, delay));
	}

	@Override
	public ITask executeAsyncDelayedTask(Runnable runnable, long delay) {
		return new Task(scheduler.executeAsyncDelayedTask(runnable, delay));
	}

}
