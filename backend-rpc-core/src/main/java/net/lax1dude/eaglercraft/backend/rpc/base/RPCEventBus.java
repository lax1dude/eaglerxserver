package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

import com.google.common.collect.Sets;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.rpc.api.EnumSubscribeEvents;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayerRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.IRPCEventHandler;

public class RPCEventBus<PlayerObject> {

	private final IEaglerPlayerRPC<PlayerObject> owner;
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private int subscribed = 0;
	private Set<IRPCEventHandler<PlayerObject, ? extends IRPCEvent>>[] handlers = new Set[3];

	public RPCEventBus(IEaglerPlayerRPC<PlayerObject> owner) {
		this.owner = owner;
	}

	public void dispatchEvent(IRPCEvent event, IPlatformLogger logger) {
		EnumSubscribeEvents eventType = event.getEventType();
		Object[] tmp;
		lock.readLock().lock();
		try {
			Set<IRPCEventHandler<PlayerObject, ? extends IRPCEvent>> h = handlers[eventType.getId()];
			if(h == null) {
				return;
			}
			tmp = h.toArray();
		}finally {
			lock.readLock().unlock();
		}
		for(int i = 0; i < tmp.length; ++i) {
			IRPCEventHandler<PlayerObject, IRPCEvent> evt = (IRPCEventHandler<PlayerObject, IRPCEvent>) tmp[i];
			try {
				evt.handleEvent(owner, eventType, event);
			}catch(Exception ex) {
				logger.error("Caught exception while dispatching RPC event to handler: " + evt, ex);
			}
		}
	}

	public <T> void dispatchLazyEvent(EnumSubscribeEvents eventType, T event, Function<T, IRPCEvent> conv, IPlatformLogger logger) {
		Object[] tmp;
		lock.readLock().lock();
		try {
			Set<IRPCEventHandler<PlayerObject, ? extends IRPCEvent>> h = handlers[eventType.getId()];
			if(h == null) {
				return;
			}
			tmp = h.toArray();
		}finally {
			lock.readLock().unlock();
		}
		int j = tmp.length;
		if(j > 0) {
			IRPCEvent evtObj = conv.apply(event);
			for(int i = 0; i < j; ++i) {
				IRPCEventHandler<PlayerObject, IRPCEvent> evt = (IRPCEventHandler<PlayerObject, IRPCEvent>) tmp[i];
				try {
					evt.handleEvent(owner, eventType, evtObj);
				}catch(Exception ex) {
					logger.error("Caught exception while dispatching RPC event to handler: " + evt, ex);
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
			if(h == null) {
				handlers[id] = h = Sets.newIdentityHashSet();
				h.add(handler);
				return subscribed |= eventType.getBit();
			}else {
				h.add(handler);
				return -1;
			}
		}finally {
			lock.writeLock().unlock();
		}
	}

	public int removeEventListener(EnumSubscribeEvents eventType,
			IRPCEventHandler<PlayerObject, ? extends IRPCEvent> handler) {
		int id = eventType.getId();
		lock.writeLock().lock();
		try {
			Set<IRPCEventHandler<PlayerObject, ? extends IRPCEvent>> h = handlers[id];
			if(h != null && h.remove(handler) && h.isEmpty()) {
				handlers[id] = null;
				return subscribed ^= eventType.getBit();
			}else {
				return -1;
			}
		}finally {
			lock.writeLock().unlock();
		}
	}

}
