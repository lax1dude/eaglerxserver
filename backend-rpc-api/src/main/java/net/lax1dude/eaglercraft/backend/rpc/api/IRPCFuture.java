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

package net.lax1dude.eaglercraft.backend.rpc.api;

import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public interface IRPCFuture<V> extends ListenableFuture<V> {

	@Nonnull
	Executor getScheduler();

	@Nonnull
	Executor getSchedulerAsync();

	@Nonnull
	Executor getSchedulerTiny();

	/**
	 * Warning: Futures.addCallback is recommended!
	 */
	default void addListener(@Nonnull Runnable runnable) {
		addListener(runnable, getScheduler());
	}

	default void addListenerAsync(@Nonnull Runnable runnable) {
		addListener(runnable, getSchedulerAsync());
	}

	default void addListenerTiny(@Nonnull Runnable runnable) {
		addListener(runnable, getSchedulerTiny());
	}

	default void addCallback(@Nonnull FutureCallback<? super V> callback, @Nonnull Executor executor) {
		Futures.addCallback(this, callback, executor);
	}

	default void addCallback(@Nonnull FutureCallback<? super V> callback) {
		Futures.addCallback(this, callback, getScheduler());
	}

	default void addCallbackAsync(@Nonnull FutureCallback<? super V> callback) {
		Futures.addCallback(this, callback, getSchedulerAsync());
	}

	default void addCallbackTiny(@Nonnull FutureCallback<? super V> callback) {
		Futures.addCallback(this, callback, getSchedulerTiny());
	}

	boolean isTimedOut();

}
