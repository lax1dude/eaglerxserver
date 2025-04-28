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

import com.google.common.util.concurrent.AbstractFuture;

public class RPCActiveFuture<V> extends AbstractFuture<V> implements IRPCFutureExpiring<V> {

	public static <V> RPCActiveFuture<V> create(SchedulerExecutors executors, long now, int expiresAfter) {
		return new RPCActiveFuture<>(executors, now + expiresAfter * 1000000000l);
	}

	private final SchedulerExecutors executors;
	private final long expiresAt;
	private boolean timedOut;

	RPCActiveFuture(SchedulerExecutors executors, long expiresAt) {
		this.executors = executors;
		this.expiresAt = expiresAt;
	}

	@Override
	public SchedulerExecutors getSchedulerExecutors() {
		return executors;
	}

	@Override
	public long expiresAt() {
		return expiresAt;
	}

	public boolean fireCompleteInternal(V value) {
		return set(value);
	}

	public boolean fireExceptionInternal(Throwable value) {
		return setException(value);
	}

	@Override
	public boolean fireTimeoutExceptionInternal(Throwable value) {
		if (!isDone()) {
			timedOut = true;
			if (fireExceptionInternal(value)) {
				return true;
			} else {
				timedOut = false; // oops
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isTimedOut() {
		return timedOut;
	}

}
