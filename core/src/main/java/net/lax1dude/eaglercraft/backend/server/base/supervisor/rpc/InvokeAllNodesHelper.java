package net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.NodeResult;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;

class InvokeAllNodesHelper<Out extends ISupervisorData> extends ArrayList<NodeResult<? super Out>>
		implements Consumer<Collection<NodeResult<? super Out>>> {

	private final Consumer<Collection<NodeResult<? super Out>>> res;
	private final IPlatformScheduler sched;
	private final IPlatformLogger logger;
	private int cntDown = 2;

	public InvokeAllNodesHelper(Consumer<Collection<NodeResult<? super Out>>> res, IPlatformScheduler sched,
			IPlatformLogger logger) {
		super(8);
		this.res = res;
		this.sched = sched;
		this.logger = logger;
	}

	@Override
	public void accept(Collection<NodeResult<? super Out>> t) {
		synchronized(this) {
			if(cntDown > 0) {
				addAll(t);
				if(--cntDown > 0) {
					return;
				}
			}else {
				return;
			}
		}
		done();
	}

	public void acceptLocal(NodeResult<? super Out> loc) {
		synchronized(this) {
			if(cntDown > 0) {
				add(loc);
				if(--cntDown > 0) {
					return;
				}
			}else {
				return;
			}
		}
		done();
	}

	private void done() {
		sched.executeAsync(() -> {
			try {
				res.accept(this);
			}catch(Exception ex) {
				logger.error("Caught exception from RPC result callback", ex);
			}
		});
	}

}
