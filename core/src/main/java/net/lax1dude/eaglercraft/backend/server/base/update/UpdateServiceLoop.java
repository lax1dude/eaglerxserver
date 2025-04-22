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

package net.lax1dude.eaglercraft.backend.server.base.update;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;

public class UpdateServiceLoop {

	public static final int MAX_QUEUE_LENGTH = 2048;

	private final IPlatformScheduler scheduler;
	private final int tickDataRateLimit;
	private IPlatformTask task;

	private final Queue<IUpdateServiceLoopRunnable> queue = new ConcurrentLinkedQueue<>();

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
		queue.add(runnable);
		if(queue.size() > MAX_QUEUE_LENGTH) {
			IUpdateServiceLoopRunnable itm = queue.poll();
			if(itm != null) {
				itm.run();
			}
		}
	}

	private void loop() {
		int total = 0;
		while(total < tickDataRateLimit) {
			IUpdateServiceLoopRunnable itm = queue.poll();
			if(itm != null) {
				total += itm.run();
			}else {
				break;
			}
		}
	}

}
