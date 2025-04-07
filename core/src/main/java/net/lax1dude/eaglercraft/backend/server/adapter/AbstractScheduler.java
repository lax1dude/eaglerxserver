package net.lax1dude.eaglercraft.backend.server.adapter;

public abstract class AbstractScheduler implements IPlatformScheduler {

	private static abstract class RepeatingTaskBase implements IPlatformTask {

		private final Runnable runnable;
		private final Runnable reschedule;
		private IPlatformTask task;

		protected RepeatingTaskBase(Runnable runnable, long interval) {
			this.runnable = runnable;
			this.reschedule = () -> {
				if(task == null) {
					return;
				}
				long start = System.nanoTime();
				try {
					runnable.run();
				}finally {
					if(task == null) {
						return;
					}
					synchronized(this) {
						if(task == null) {
							return;
						}
						task = reschedule(getReschedule(), Math.max(interval - ((System.nanoTime() - start) / 1000000l), 5l));
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
			if(task == null) {
				return;
			}
			IPlatformTask t;
			synchronized(this) {
				t = task;
				if(t == null) {
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
		return (new RepeatingTask(runnable, interval)).bootstrap(delay);
	}

	@Override
	public IPlatformTask executeAsyncRepeatingTask(Runnable runnable, long delay, long interval) {
		return (new RepeatingTaskAsync(runnable, interval)).bootstrap(delay);
	}

}
