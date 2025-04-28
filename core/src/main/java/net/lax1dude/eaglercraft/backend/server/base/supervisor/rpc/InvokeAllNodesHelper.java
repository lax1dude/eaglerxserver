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

package net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformScheduler;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.NodeResult;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;

class InvokeAllNodesHelper<Out extends ISupervisorData> extends ArrayList<NodeResult<Out>>
		implements Consumer<Collection<NodeResult<Out>>> {

	private final Consumer<? super Collection<NodeResult<Out>>> res;
	private final IPlatformScheduler sched;
	private final IPlatformLogger logger;
	private int cntDown = 2;

	public InvokeAllNodesHelper(Consumer<? super Collection<NodeResult<Out>>> res, IPlatformScheduler sched,
			IPlatformLogger logger) {
		super(8);
		this.res = res;
		this.sched = sched;
		this.logger = logger;
	}

	@Override
	public void accept(Collection<NodeResult<Out>> t) {
		synchronized (this) {
			if (cntDown > 0) {
				if (t != null) {
					addAll(t);
				}
				if (--cntDown > 0) {
					return;
				}
			} else {
				return;
			}
		}
		done();
	}

	public void acceptLocal(NodeResult<Out> loc) {
		synchronized (this) {
			if (cntDown > 0) {
				add(loc);
				if (--cntDown > 0) {
					return;
				}
			} else {
				return;
			}
		}
		done();
	}

	private void done() {
		sched.executeAsync(() -> {
			try {
				res.accept(this);
			} catch (Exception ex) {
				logger.error("Caught exception from RPC result callback", ex);
			}
		});
	}

}
