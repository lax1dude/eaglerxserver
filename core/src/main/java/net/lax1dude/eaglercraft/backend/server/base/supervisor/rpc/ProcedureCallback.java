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
