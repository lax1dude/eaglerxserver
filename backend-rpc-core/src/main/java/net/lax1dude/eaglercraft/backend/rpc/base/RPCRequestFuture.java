package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.concurrent.ConcurrentMap;

public class RPCRequestFuture<V> extends RPCActiveFuture<V> {

	protected final Integer requestId;
	protected final ConcurrentMap<Integer, RPCRequestFuture<?>> map;

	public RPCRequestFuture(SchedulerExecutors exec, long expiresAt, Integer requestId,
			ConcurrentMap<Integer, RPCRequestFuture<?>> map) {
		super(exec, expiresAt);
		this.requestId = requestId;
		this.map = map;
	}

	public int getRequestId() {
		return requestId;
	}

	public boolean fireResponseInternal(Object value) {
		return fireCompleteInternal((V) value);
	}

	public boolean fireCompleteInternal(V value) {
		if(super.fireCompleteInternal(value)) {
			eaglerCleanup();
			return true;
		}else {
			return false;
		}
	}

	public boolean fireExceptionInternal(Throwable value) {
		if(super.fireExceptionInternal(value)) {
			eaglerCleanup();
			return true;
		}else {
			return false;
		}
	}

	protected void eaglerCleanup() {
		map.remove(requestId);
	}

}
