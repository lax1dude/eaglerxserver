package net.lax1dude.eaglercraft.backend.rpc.bukkit;

import org.bukkit.Server;
import org.bukkit.scheduler.BukkitTask;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformTask;

class BukkitScheduler implements IPlatformScheduler {

	private final PlatformPluginBukkit plugin;
	private final Server server;
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
		this.server = plugin.getServer();
		this.scheduler = scheduler;
	}

	@Override
	public void execute(Runnable runnable) {
		if(server.isPrimaryThread()) {
			try {
				runnable.run();
			}catch(Exception ex) {
				plugin.logger().error("Scheduler failed to execute a task immediately", ex);
			}
		}else {
			scheduler.runTask(plugin, runnable);
		}
	}

	@Override
	public void executeAsync(Runnable runnable) {
		scheduler.runTaskAsynchronously(plugin, runnable);
	}

	@Override
	public void executeDelayed(Runnable runnable, long delay) {
		scheduler.runTaskLater(plugin, runnable, delay / 50l);
	}

	@Override
	public void executeAsyncDelayed(Runnable runnable, long delay) {
		scheduler.runTaskLaterAsynchronously(plugin, runnable, delay / 50l);
	}

	@Override
	public IPlatformTask executeDelayedTask(Runnable runnable, long delay) {
		return new Task(scheduler.runTaskLater(plugin, runnable, delay / 50l), runnable);
	}

	@Override
	public IPlatformTask executeAsyncDelayedTask(Runnable runnable, long delay) {
		return new Task(scheduler.runTaskLaterAsynchronously(plugin, runnable, delay / 50l), runnable);
	}

	@Override
	public IPlatformTask executeRepeatingTask(Runnable runnable, long delay, long interval) {
		return new Task(scheduler.runTaskTimer(plugin, runnable, delay / 50l, interval / 50l), runnable);
	}

	@Override
	public IPlatformTask executeAsyncRepeatingTask(Runnable runnable, long delay, long interval) {
		return new Task(scheduler.runTaskTimerAsynchronously(plugin, runnable, delay / 50l, interval / 50l), runnable);
	}

}
