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

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import com.google.common.collect.Sets;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumExecutorType;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEventHandler;

public class RPCEventBus<PlayerObject> {

	private final IEaglerPlayerRPC<PlayerObject> owner;
	private final IPlatformScheduler scheduler;
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private int subscribed = 0;
	private Set<IRPCEventHandler<PlayerObject, ? extends IRPCEvent>>[] handlers = new Set[EnumSubscribeEvents.total];

	public RPCEventBus(IEaglerPlayerRPC<PlayerObject> owner, IPlatformScheduler scheduler) {
		this.owner = owner;
		this.scheduler = scheduler;
	}

	private class RPCEventWrapper<T extends IRPCEvent> implements Runnable {

		private final IRPCEventHandler<PlayerObject, T> handler;
		private final IPlatformLogger logger;
		private final T event;

		private RPCEventWrapper(IRPCEventHandler<PlayerObject, T> handler, IPlatformLogger logger, T event) {
			this.handler = handler;
			this.logger = logger;
			this.event = event;
		}

		@Override
		public void run() {
			try {
				handler.handleEvent(owner, event.getEventType(), event);
			} catch (Exception ex) {
				logger.error("Caught exception while dispatching RPC event to handler: " + handler, ex);
			}
		}

	}

	public <T extends IRPCEvent> void dispatchEvent(T event, IPlatformLogger logger) {
		EnumSubscribeEvents eventType = event.getEventType();
		Object[] tmp;
		lock.readLock().lock();
		try {
			Set<IRPCEventHandler<PlayerObject, ? extends IRPCEvent>> h = handlers[eventType.getId()];
			if (h == null) {
				return;
			}
			tmp = h.toArray();
		} finally {
			lock.readLock().unlock();
		}
		for (int i = 0; i < tmp.length; ++i) {
			IRPCEventHandler<PlayerObject, T> handler = (IRPCEventHandler<PlayerObject, T>) tmp[i];
			RPCEventWrapper<T> evt = new RPCEventWrapper<>(handler, logger, event);
			EnumExecutorType type = handler.getExecutor();
			if (type == EnumExecutorType.SYNC) {
				scheduler.execute(evt);
			} else if (type == EnumExecutorType.ASYNC) {
				scheduler.executeAsync(evt);
			} else {
				evt.run();
			}
		}
	}

	public <I, T extends IRPCEvent> void dispatchLazyEvent(EnumSubscribeEvents eventType, I event, Function<I, T> conv,
			IPlatformLogger logger) {
		Object[] tmp;
		lock.readLock().lock();
		try {
			Set<IRPCEventHandler<PlayerObject, ? extends IRPCEvent>> h = handlers[eventType.getId()];
			if (h == null) {
				return;
			}
			tmp = h.toArray();
		} finally {
			lock.readLock().unlock();
		}
		int j = tmp.length;
		if (j > 0) {
			T evtObj = conv.apply(event);
			for (int i = 0; i < j; ++i) {
				IRPCEventHandler<PlayerObject, T> handler = (IRPCEventHandler<PlayerObject, T>) tmp[i];
				RPCEventWrapper<T> evt = new RPCEventWrapper<>(handler, logger, evtObj);
				EnumExecutorType type = handler.getExecutor();
				if (type == EnumExecutorType.SYNC) {
					scheduler.execute(evt);
				} else if (type == EnumExecutorType.ASYNC) {
					scheduler.executeAsync(evt);
				} else {
					evt.run();
				}
			}
		}
	}

	public int addEventListener(EnumSubscribeEvents eventType,
			IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler) {
		int id = eventType.getId();
		lock.writeLock().lock();
		try {
			Set<IRPCEventHandler<PlayerObject, ? extends IRPCEvent>> h = handlers[id];
			if (h == null) {
				handlers[id] = h = Sets.newIdentityHashSet();
				h.add(handler);
				return subscribed |= eventType.getBit();
			} else {
				h.add(handler);
				return -1;
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public int removeEventListener(EnumSubscribeEvents eventType,
			IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler) {
		int id = eventType.getId();
		lock.writeLock().lock();
		try {
			Set<IRPCEventHandler<PlayerObject, ? extends IRPCEvent>> h = handlers[id];
			if (h != null && h.remove(handler) && h.isEmpty()) {
				handlers[id] = null;
				return subscribed ^= eventType.getBit();
			} else {
				return -1;
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

}
