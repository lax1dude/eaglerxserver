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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.lax1dude.eaglercraft.backend.rpc.api.RPCTimeoutException;

public class RPCFailedFuture<V> implements IRPCFutureAbstract<V> {

	public static <V> RPCFailedFuture<V> create(SchedulerExecutors executors, Throwable error) {
		return new RPCFailedFuture<>(executors, error);
	}

	public static <V> RPCFailedFuture<V> createClosed(SchedulerExecutors executors) {
		return create(executors, new RPCTimeoutException("RPC context is closed"));
	}

	private final SchedulerExecutors executors;
	private final Throwable error;

	private RPCFailedFuture(SchedulerExecutors executors, Throwable error) {
		this.executors = executors;
		this.error = error;
	}

	@Override
	public SchedulerExecutors getSchedulerExecutors() {
		return executors;
	}

	@Override
	public void addListener(Runnable arg0, Executor arg1) {
		arg1.execute(arg0);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		throw new ExecutionException(error);
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		throw new ExecutionException(error);
	}

	@Override
	public boolean isTimedOut() {
		return error instanceof RPCTimeoutException;
	}

}
