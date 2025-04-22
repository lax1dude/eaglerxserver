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

package net.lax1dude.eaglercraft.backend.server.bungee;

import java.util.concurrent.TimeUnit;

import net.lax1dude.eaglercraft.backend.server.adapter.AbstractScheduler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;

class BungeeScheduler extends AbstractScheduler {

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
	public void execute(Runnable runnable) {
		scheduler.runAsync(plugin, runnable);
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
