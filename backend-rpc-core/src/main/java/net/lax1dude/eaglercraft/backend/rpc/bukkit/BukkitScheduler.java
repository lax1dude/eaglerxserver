/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

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
