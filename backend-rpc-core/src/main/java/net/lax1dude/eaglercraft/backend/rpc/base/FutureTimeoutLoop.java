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

package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

import com.google.common.collect.MapMaker;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.rpc.api.RPCTimeoutException;

public class FutureTimeoutLoop {

	private class TimeoutEvent implements Runnable {

		protected final Long key;

		// The timeoutEvents ConcurrentMap compute method will synchronize this
		protected final List<IRPCFutureExpiring<?>> queue = new ArrayList<>();

		protected TimeoutEvent(Long key) {
			this.key = key;
		}

		@Override
		public void run() {
			if(timeoutEvents.remove(key) != null) {
				for(int i = 0, l = queue.size(); i < l; ++i) {
					IRPCFutureExpiring<?> future = queue.get(i);
					if(!future.isDone()) {
						expire(future);
					}
				}
			}
		}

	}

	private final ConcurrentMap<Long, TimeoutEvent> timeoutEvents = (new MapMaker()).initialCapacity(128)
			.concurrencyLevel(8).makeMap();

	private final IPlatformScheduler scheduler;
	private final long resolution;

	public FutureTimeoutLoop(IPlatformScheduler scheduler, long resolution) {
		this.scheduler = scheduler;
		this.resolution = resolution;
	}

	private class Witness implements BiFunction<Long, TimeoutEvent, TimeoutEvent> {

		protected final IRPCFutureExpiring<?> future;
		protected boolean schedule = false;

		protected Witness(IRPCFutureExpiring<?> future) {
			this.future = future;
		}

		@Override
		public TimeoutEvent apply(Long k, TimeoutEvent v) {
			if(v != null) {
				v.queue.add(future);
				return v;
			}else {
				TimeoutEvent te = new TimeoutEvent(k);
				te.queue.add(future);
				schedule = true;
				return te;
			}
		}

	}

	public void addFuture(IRPCFutureExpiring<?> future) {
		addFuture(System.nanoTime(), future);
	}

	public void addFuture(long now, IRPCFutureExpiring<?> future) {
		long expires = future.expiresAt();
		if(now >= expires) {
			expire(future);
			return;
		}
		long bucket = (expires + (resolution - 1)) / resolution;
		Witness witness = new Witness(future);
		TimeoutEvent te = timeoutEvents.compute(bucket, witness);
		if(witness.schedule) {
			long l = (bucket * resolution - now) / 1000000l;
			scheduler.executeAsyncDelayed(te, l > 0l ? l : 0l);
		}
	}

	private void expire(IRPCFutureExpiring<?> future) {
		future.fireTimeoutExceptionInternal(new RPCTimeoutException("RPC operation reached timeout"));
	}

	public void cancelAll() {
		timeoutEvents.clear();
	}

}
