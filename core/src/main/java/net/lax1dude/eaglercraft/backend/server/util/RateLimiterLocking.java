package net.lax1dude.eaglercraft.backend.server.util;

import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiterLocking extends AtomicInteger {

	public static class Config {

		public final long period;
		public final int limit;
		public final int limitLockout;
		public final long lockoutDuration;

		public Config(int period, int limit, int limitLockout, long lockoutDuration) {
			this.period = period * 1000000000l;
			this.limit = limit;
			this.limitLockout = limitLockout;
			this.lockoutDuration = lockoutDuration * 1000000000l;
		}

	}

	private long timer;
	private long lockedTimer;

	public RateLimiterLocking() {
		super(0);
		this.timer = System.nanoTime();
		this.lockedTimer = -1l;
	}

	public EnumRateLimitState rateLimit(Config conf) {
		int limitVal = conf.limit;
		if(incrementAndGet() >= limitVal) {
			synchronized(this) {
				int v = getPlain();
				if(v < limitVal) {
					return EnumRateLimitState.OK;
				}
				long now = System.nanoTime();
				if(lockedTimer != -1l) {
					if(now - lockedTimer > conf.lockoutDuration) {
						lockedTimer = -1l;
						setPlain(0);
						return EnumRateLimitState.OK;
					}
					return EnumRateLimitState.LOCKED;
				}else {
					if(v >= conf.limitLockout) {
						lockedTimer = now;
						return EnumRateLimitState.BLOCKED_LOCKED;
					}
					long period = (long)(conf.period / limitVal);
					long delta = (now - timer) / period;
					if(delta > 0l) {
						timer += delta * period;
						return addAndGet(-Math.min((int)delta, v)) < limitVal ? EnumRateLimitState.BLOCKED
								: EnumRateLimitState.OK;
					}
					return EnumRateLimitState.BLOCKED;
				}
			}
		}else {
			return EnumRateLimitState.OK;
		}
	}

}
