package net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc;

import java.util.Set;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.base.supervisor.ISupervisorExpiring;

abstract class LocalTimeout<Out> implements Consumer<Out>, ISupervisorExpiring {

	protected final Set<LocalTimeout<?>> set;
	protected final long timeout;

	protected LocalTimeout(Set<LocalTimeout<?>> set, long timeout) {
		this.set = set;
		this.timeout = timeout;
	}

	protected abstract void onResultTimeout();

	protected abstract void onResultComplete(Out data);

	@Override
	public long expiresAt() {
		return timeout;
	}

	@Override
	public void expire() {
		if(set.remove(this)) {
			onResultTimeout();
		}
	}

	@Override
	public void accept(Out res) {
		if(set.remove(this)) {
			onResultComplete(res);
		}
	}

}
