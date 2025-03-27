package net.lax1dude.eaglercraft.backend.server.util;

import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiter extends AtomicInteger {

	private long timer;

	public RateLimiter() {
		super(0);
		this.timer = Util.steadyTime();
	}

	public boolean rateLimit(int limitVal) {
		if(incrementAndGet() >= limitVal) {
			synchronized(this) {
				int v = get();
				if(v < limitVal) {
					return false;
				}
				long period = (long)(60000 / limitVal);
				long delta = (Util.steadyTime() - timer) / period;
				if(delta > 0l) {
					timer += delta * period;
					return addAndGet(-Math.min((int)delta, v)) < limitVal;
				}
				return false;
			}
		}else {
			return true;
		}
	}

}
