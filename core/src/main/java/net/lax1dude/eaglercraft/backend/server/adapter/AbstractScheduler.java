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

package net.lax1dude.eaglercraft.backend.server.adapter;

public abstract class AbstractScheduler implements IPlatformScheduler {

	private static abstract class RepeatingTaskBase implements IPlatformTask {

		private final Runnable runnable;
		private final Runnable reschedule;
		private IPlatformTask task;

		protected RepeatingTaskBase(Runnable runnable, long interval) {
			this.runnable = runnable;
			this.reschedule = () -> {
				if (task == null) {
					return;
				}
				long start = System.nanoTime();
				try {
					runnable.run();
				} finally {
					if (task == null) {
						return;
					}
					synchronized (this) {
						if (task == null) {
							return;
						}
						task = reschedule(getReschedule(),
								Math.max(interval - ((System.nanoTime() - start) / 1000000l), 5l));
					}
				}
			};
		}

		@Override
		public Runnable getTask() {
			return runnable;
		}

		@Override
		public void cancel() {
			if (task == null) {
				return;
			}
			IPlatformTask t;
			synchronized (this) {
				t = task;
				if (t == null) {
					return;
				}
				task = null;
			}
			t.cancel();
		}

		protected Runnable getReschedule() {
			return reschedule;
		}

		protected IPlatformTask bootstrap(long delay) {
			task = reschedule(reschedule, delay);
			return this;
		}

		protected abstract IPlatformTask reschedule(Runnable runnable, long delay);

	}

	private class RepeatingTask extends RepeatingTaskBase {

		protected RepeatingTask(Runnable runnable, long interval) {
			super(runnable, interval);
		}

		@Override
		protected IPlatformTask reschedule(Runnable runnable, long delay) {
			return AbstractScheduler.this.executeDelayedTask(runnable, delay);
		}

	}

	private class RepeatingTaskAsync extends RepeatingTaskBase {

		protected RepeatingTaskAsync(Runnable runnable, long interval) {
			super(runnable, interval);
		}

		@Override
		protected IPlatformTask reschedule(Runnable runnable, long delay) {
			return AbstractScheduler.this.executeAsyncDelayedTask(runnable, delay);
		}

	}

	@Override
	public IPlatformTask executeRepeatingTask(Runnable runnable, long delay, long interval) {
		if (runnable == null) {
			throw new NullPointerException("runnable");
		}
		return (new RepeatingTask(runnable, interval)).bootstrap(delay);
	}

	@Override
	public IPlatformTask executeAsyncRepeatingTask(Runnable runnable, long delay, long interval) {
		if (runnable == null) {
			throw new NullPointerException("runnable");
		}
		return (new RepeatingTaskAsync(runnable, interval)).bootstrap(delay);
	}

}
