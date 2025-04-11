package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

import com.google.common.collect.MapMaker;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformScheduler;

public class SupervisorTimeoutLoop {

	private class TimeoutEvent implements Runnable {

		protected final Long key;

		// The timeoutEvents ConcurrentMap compute method will synchronize this
		protected final List<ISupervisorExpiring> queue = new ArrayList<>();

		protected TimeoutEvent(Long key) {
			this.key = key;
		}

		@Override
		public void run() {
			if(timeoutEvents.remove(key) != null) {
				for(int i = 0, l = queue.size(); i < l; ++i) {
					queue.get(i).expire();
				}
			}
		}

	}

	private final ConcurrentMap<Long, TimeoutEvent> timeoutEvents = (new MapMaker()).initialCapacity(256)
			.concurrencyLevel(16).makeMap();

	private final IPlatformScheduler scheduler;
	private final long resolution;

	public SupervisorTimeoutLoop(IPlatformScheduler scheduler, long resolution) {
		this.scheduler = scheduler;
		this.resolution = resolution;
	}

	private class Witness implements BiFunction<Long, TimeoutEvent, TimeoutEvent> {

		protected final ISupervisorExpiring future;
		protected boolean schedule = false;

		protected Witness(ISupervisorExpiring future) {
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

	public void addFuture(ISupervisorExpiring future) {
		addFuture(System.nanoTime(), future);
	}

	public void addFuture(long now, ISupervisorExpiring future) {
		long expires = future.expiresAt();
		if(now >= expires) {
			future.expire();
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

	public void cancelAll() {
		timeoutEvents.clear();
	}

}
