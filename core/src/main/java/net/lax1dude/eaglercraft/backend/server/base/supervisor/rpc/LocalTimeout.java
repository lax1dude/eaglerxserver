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
