package net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.data.ISupervisorData;
import net.lax1dude.eaglercraft.backend.server.base.message.ByteBufInputWrapper;
import net.lax1dude.eaglercraft.backend.server.base.message.ByteBufOutputWrapper;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.util.IInjectedPayload;

class InjectedRPCPayload implements IInjectedPayload {

	private final String name;
	private final ISupervisorData data;

	InjectedRPCPayload(String name, ISupervisorData data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public int writePayload(ByteBuf buf) {
		int ret = buf.writeCharSequence(name, StandardCharsets.US_ASCII);
		try {
			data.write(new ByteBufOutputWrapper(buf));
		} catch (Exception e) {
			throw new IllegalStateException("Failed to serialize supervisor data: " + data, e);
		}
		return ret;
	}

	static ISupervisorData deserialize(ByteBuf buf, SupervisorDataType dataType) throws Exception {
		if(dataType == SupervisorDataType.VOID_TYPE) {
			return ISupervisorData.VOID;
		}else {
			if(buf == null) {
				buf = Unpooled.EMPTY_BUFFER;
			}
			ISupervisorData ret;
			try {
				ret = dataType.ctor.newInstance();
			} catch (ReflectiveOperationException e) {
				throw Util.propagateReflectThrowable(e);
			}
			ret.read(new ByteBufInputWrapper(buf));
			return ret;
		}
	}

}
