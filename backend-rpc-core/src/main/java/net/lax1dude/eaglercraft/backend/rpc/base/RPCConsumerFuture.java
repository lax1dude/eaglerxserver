package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.function.Consumer;

import com.google.common.util.concurrent.AbstractFuture;

import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;

public abstract class RPCConsumerFuture<I, V> extends AbstractFuture<V> implements IRPCFuture<V>, Consumer<I> {

	@Override
	public void setExpiresMSFromNow(int millis) {
	}

	@Override
	public boolean hasExpired() {
		return false;
	}

}
