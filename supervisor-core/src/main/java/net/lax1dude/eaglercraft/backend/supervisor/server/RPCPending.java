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

package net.lax1dude.eaglercraft.backend.supervisor.server;

import java.util.Map;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.supervisor.server.TimeoutLoop.IExpirable;

abstract class RPCPending implements IExpirable {

	public static final int FAILURE_PROCEDURE = 0;
	public static final int FAILURE_TIMEOUT = 1;
	public static final int FAILURE_HANGUP = 2;

	protected final long timeout;
	protected final UUID key;
	protected Map<UUID, RPCPending> map;

	protected RPCPending(UUID key, long timeout) {
		this.key = key;
		this.timeout = timeout;
	}

	@Override
	public long expiresAt() {
		return timeout;
	}

	@Override
	public void expire() {
		if (map.remove(key) != null) {
			onFailure(FAILURE_TIMEOUT);
		}
	}

	protected abstract void onSuccess(ByteBuf dataBuffer);

	protected abstract void onFailure(int type);

}