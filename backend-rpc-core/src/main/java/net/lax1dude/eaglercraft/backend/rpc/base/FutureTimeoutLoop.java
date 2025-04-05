package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.rpc.api.RPCTimeoutException;

public class FutureTimeoutLoop {

	private static final long RESOLUTION = 250l * 1000000l;

	private class TimeoutEvent implements Runnable {

		protected final Long key;

		// The timeoutEvents ConcurrentMap compute method will synchronize this
		protected final List<IRPCFutureExpiring<?>> queue = new ArrayList<>();

		protected TimeoutEvent(Long key) {
			this.key = key;
		}

		@Override
		public void run() {
			timeoutEvents.remove(key);
			for(int i = 0, l = queue.size(); i < l; ++i) {
				IRPCFutureExpiring<?> future = queue.get(i);
				if(!future.isDone()) {
					expire(future);
				}
			}
		}

	}

	private final ConcurrentMap<Long, TimeoutEvent> timeoutEvents = new ConcurrentHashMap<>();

	private final IPlatformScheduler scheduler;

	public FutureTimeoutLoop(IPlatformScheduler scheduler) {
		this.scheduler = scheduler;
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
		long bucket = (expires + (RESOLUTION - 1)) / RESOLUTION;
		Witness witness = new Witness(future);
		TimeoutEvent te = timeoutEvents.compute(bucket, witness);
		if(witness.schedule) {
			long l = (bucket * RESOLUTION - now) / 1000l;
			scheduler.executeAsyncDelayed(te, l > 0l ? l : 1l);
		}
	}

	private void expire(IRPCFutureExpiring<?> future) {
		future.fireExceptionInternal(new RPCTimeoutException("RPC operation reached timeout"));
	}

}
