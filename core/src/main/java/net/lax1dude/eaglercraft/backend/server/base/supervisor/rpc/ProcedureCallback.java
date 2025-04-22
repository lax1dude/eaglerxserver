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

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.ISupervisorExpiring;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvRPCResultMulti;

abstract class ProcedureCallback implements ISupervisorExpiring {

	final UUID key;
	private ConcurrentMap<UUID, ProcedureCallback> map;
	private final long expires;

	ProcedureCallback(UUID key, ConcurrentMap<UUID, ProcedureCallback> map, long expires) {
		this.key = key;
		this.map = map;
		this.expires = expires;
	}

	@Override
	public long expiresAt() {
		return expires;
	}

	@Override
	public void expire() {
		if(map.remove(key) != null) {
			onResultFail(-1);
		}
	}

	protected abstract void onResultFail(int type);

	protected abstract void onResultSuccess(ByteBuf dataBuffer);

	protected abstract void onResultMulti(Collection<SPacketSvRPCResultMulti.ResultEntry> list);

}
