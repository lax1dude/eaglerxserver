package net.lax1dude.eaglercraft.backend.server.util;

import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiterLocking extends AtomicInteger {

	public static class Config {

		public final int period;
		public final int limit;
		public final int limitLockout;
		public final long lockoutDuration;

		public Config(int period, int limit, int limitLockout, long lockoutDuration) {
			this.period = period;
			this.limit = limit;
			this.limitLockout = limitLockout;
			this.lockoutDuration = lockoutDuration;
		}

	}

	private long timer;
	private long lockedTimer;

	public RateLimiterLocking() {
		super(0);
		this.timer = Util.steadyTime();
		this.lockedTimer = -1l;
	}

	public boolean rateLimit(Config conf) {
		int limitVal = conf.limit;
		if(incrementAndGet() >= limitVal) {
			synchronized(this) {
				int v = get();
				if(v < limitVal) {
					return false;
				}
				long now = Util.steadyTime();
				if(lockedTimer != -1l) {
					if(now - lockedTimer > conf.lockoutDuration) {
						lockedTimer = -1l;
						set(0);
						return true;
					}
				}else {
					if(v >= conf.limitLockout) {
						lockedTimer = now;
						return false;
					}
					long period = (long)(conf.period / limitVal);
					long delta = (now - timer) / period;
					if(delta > 0l) {
						timer += delta * period;
						return addAndGet(-Math.min((int)delta, v)) < limitVal;
					}
				}
				return false;
			}
		}else {
			return true;
		}
	}

}
