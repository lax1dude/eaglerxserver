package net.lax1dude.eaglercraft.backend.rpc.base;

import java.util.function.BiConsumer;

import com.google.common.util.concurrent.AbstractFuture;

import net.lax1dude.eaglercraft.backend.rpc.api.IRPCFuture;

public abstract class RPCBiConsumerFuture<I1, I2, V> extends AbstractFuture<V> implements IRPCFuture<V>, BiConsumer<I1, I2> {

	@Override
	public void setExpiresMSFromNow(int millis) {
	}

	@Override
	public boolean hasExpired() {
		return false;
	}

}
