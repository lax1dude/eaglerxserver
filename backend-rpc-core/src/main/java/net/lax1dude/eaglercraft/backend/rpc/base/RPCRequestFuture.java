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
