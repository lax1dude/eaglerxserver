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

package net.lax1dude.eaglercraft.backend.server.velocity;

import java.util.concurrent.TimeUnit;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;

import net.lax1dude.eaglercraft.backend.server.adapter.AbstractScheduler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;

class VelocityScheduler extends AbstractScheduler {

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
	public void execute(Runnable runnable) {
		scheduler.buildTask(plugin, runnable).schedule();
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
