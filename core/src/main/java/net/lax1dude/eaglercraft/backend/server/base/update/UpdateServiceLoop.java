package net.lax1dude.eaglercraft.backend.server.base.update;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;

public class UpdateServiceLoop {

	public static final int MAX_QUEUE_LENGTH = 2048;

	private final IPlatformScheduler scheduler;
	private final int tickDataRateLimit;
	private IPlatformTask task;

	private final Deque<IUpdateServiceLoopRunnable> queue = new ConcurrentLinkedDeque<>();

	public UpdateServiceLoop(IPlatformScheduler scheduler, int certPacketDataRateLimit) {
		this.scheduler = scheduler;
		this.tickDataRateLimit = certPacketDataRateLimit / 20;
	}

	public void start() {
		stop();
		task = scheduler.executeAsyncRepeatingTask(this::loop, 50l, 50l);
	}

	public void stop() {
		if(task != null) {
			task.cancel();
			task = null;
		}
	}

	public interface IUpdateServiceLoopRunnable {
		int run();
	}

	public void pushRunnable(IUpdateServiceLoopRunnable runnable) {
		queue.addLast(runnable);
		if(queue.size() > MAX_QUEUE_LENGTH) {
			IUpdateServiceLoopRunnable itm = queue.pollFirst();
			if(itm != null) {
				itm.run();
			}
		}
	}

	private void loop() {
		int total = 0;
		while(total < tickDataRateLimit) {
			IUpdateServiceLoopRunnable itm = queue.pollFirst();
			if(itm != null) {
				total += itm.run();
			}else {
				break;
			}
		}
	}

}
