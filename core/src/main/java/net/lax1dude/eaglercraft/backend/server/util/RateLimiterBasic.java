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

package net.lax1dude.eaglercraft.backend.server.util;

import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiterBasic extends AtomicInteger {

	private long timer;

	public RateLimiterBasic() {
		super(0);
		this.timer = System.nanoTime();
	}

	public boolean rateLimit(int limitVal) {
		return rateLimit(60l * 1000000000l, limitVal);
	}

	public boolean rateLimit(long periodNanos, int limitVal) {
		if(incrementAndGet() >= limitVal) {
			synchronized(this) {
				int v = getPlain();
				if(v < limitVal) {
					return true;
				}
				long period = (long)(60000000000l / limitVal);
				long delta = (System.nanoTime() - timer) / period;
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

	public boolean checkState(int limitVal) {
		if(getAcquire() >= limitVal) {
			synchronized(this) {
				int v = getPlain();
				if(v < limitVal) {
					return true;
				}
				long period = (long)(60000000000l / limitVal);
				long delta = (System.nanoTime() - timer) / period;
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
