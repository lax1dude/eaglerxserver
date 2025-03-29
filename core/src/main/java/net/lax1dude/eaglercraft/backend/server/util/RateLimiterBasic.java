package net.lax1dude.eaglercraft.backend.server.util;

import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiterBasic extends AtomicInteger {

	private long timer;

	public RateLimiterBasic() {
		super(0);
		this.timer = Util.steadyTime();
	}

	public boolean rateLimit(int limitVal) {
		if(incrementAndGet() >= limitVal) {
			synchronized(this) {
				int v = get();
				if(v < limitVal) {
					return true;
				}
				long period = (long)(60000 / limitVal);
				long delta = (Util.steadyTime() - timer) / period;
				if(delta > 0l) {
					timer += delta * period;
					int correction = v - (limitVal << 1);
					if(correction > 0) {
						delta += correction;
					}
					return addAndGet(-Math.min((int)delta, v)) < limitVal;
				}
				return false;
			}
		}else {
			return true;
		}
	}

}
