package net.lax1dude.eaglercraft.backend.supervisor.protocol.util;

import io.netty.buffer.ByteBuf;

public interface IInjectedPayload {

	int writePayload(ByteBuf buf);

}
