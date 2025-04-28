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

package net.lax1dude.eaglercraft.backend.supervisor.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.netty.channel.EventLoop;

public class TimeoutLoop {

	public interface IExpirable {

		long expiresAt();

		void expire();

	}

	private class TimeoutEvent implements Runnable {

		protected final Long key;

		// The timeoutEvents ConcurrentMap compute method will synchronize this
		protected final List<IExpirable> queue = new ArrayList<>();

		protected TimeoutEvent(Long key) {
			this.key = key;
		}

		@Override
		public void run() {
			if (timeoutEvents.remove(key) != null) {
				for (int i = 0, l = queue.size(); i < l; ++i) {
					queue.get(i).expire();
				}
			}
		}

	}

	private final Map<Long, TimeoutEvent> timeoutEvents = new HashMap<>(128);

	private final EventLoop scheduler;
	private final long resolution;

	public TimeoutLoop(EventLoop scheduler, long resolution) {
		this.scheduler = scheduler;
		this.resolution = resolution;
	}

	public boolean addFuture(IExpirable future) {
		return addFuture(System.nanoTime(), future);
	}

	public boolean addFuture(long now, IExpirable future) {
		long expires = future.expiresAt();
		if (now >= expires) {
			future.expire();
			return false;
		}
		long bucket = (expires + (resolution - 1)) / resolution;
		Long bucketKey = bucket;
		TimeoutEvent te = timeoutEvents.get(bucketKey);
		if (te == null) {
			te = new TimeoutEvent(bucketKey);
			te.queue.add(future);
			timeoutEvents.put(bucketKey, te);
			long l = bucket * resolution - now;
			scheduler.schedule(te, l > 0l ? l : 0l, TimeUnit.NANOSECONDS);
		} else {
			te.queue.add(future);
		}
		return true;
	}

	public void cancelAll() {
		timeoutEvents.clear();
	}

}
